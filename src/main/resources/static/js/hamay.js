(() => {
    'use strict';

    const $ = (sel, raiz = document) => raiz.querySelector(sel);
    const $$ = (sel, raiz = document) => Array.from(raiz.querySelectorAll(sel));

    // encabezado flotante que se compacta al hacer scroll
    const marcarScroll = () => {
        if (document.body) {
            document.body.classList.toggle('hw-scroll', window.scrollY > 30);
        }
    };
    marcarScroll();
    window.addEventListener('scroll', marcarScroll, { passive: true });

    // tesoros del valle acordeon horizontal
    const tesoros = $('.hw-tesoros');
    if (tesoros) {
        const items = $$('.hw-tesoro', tesoros);
        const activar = (el) => {
            items.forEach((i) => {
                const on = i === el;
                i.classList.toggle('activo', on);
                i.setAttribute('aria-expanded', String(on));
            });
            tesoros.classList.toggle('hw-hay-activo', Boolean(el));
        };
        items.forEach((i) => {
            i.addEventListener('mouseenter', () => activar(i));
            i.addEventListener('focus', () => activar(i));
            i.addEventListener('click', () => activar(i));
            i.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    activar(i);
                }
            });
        });
        tesoros.addEventListener('mouseleave', () => activar(null));
        if (items.length > 0) {
            activar(items[0]);
        }
    }

    // reservas calculo en tiempo real stepper tarifas y modal de confirmacion
    const form = $('#formReserva');
    if (form && window.bootstrap) {
        const entrada = $('#fechaEntrada');
        const salida = $('#fechaSalida');
        const hab = $('#numHabitaciones');
        const hue = $('#numHuespedes');
        const selectTipo = $('#tipoHabitacionId');

        const aDate = (v) => new Date(v + 'T00:00:00');
        const iso = (d) => {
            const p = (n) => String(n).padStart(2, '0');
            return d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate());
        };
        const sumarDias = (v, n) => {
            const d = aDate(v);
            d.setDate(d.getDate() + n);
            return iso(d);
        };
        const fmt = (v) => {
            if (!v) return '—';
            try {
                return aDate(v).toLocaleDateString('es-PE', {
                    weekday: 'short',
                    day: 'numeric',
                    month: 'short'
                }).replace(/\./g, '');
            } catch (e) {
                return v;
            }
        };
        const noches = () => {
            if (!entrada || !salida || !entrada.value || !salida.value) return 0;
            const n = Math.round((aDate(salida.value) - aDate(entrada.value)) / 86400000);
            return n > 0 ? n : 0;
        };

        const pintar = () => {
            const numHab = parseInt(hab ? hab.value : 1, 10) || 1;
            const numHue = parseInt(hue ? hue.value : 1, 10) || 1;
            const cantNoches = noches();
            const texto = numHab + ' habitación(es), ' + numHue + ' huésped(es)';

            const elEntrada = $('#resEntrada');
            if (elEntrada) elEntrada.textContent = fmt(entrada ? entrada.value : '');

            const elSalida = $('#resSalida');
            if (elSalida) elSalida.textContent = fmt(salida ? salida.value : '');

            const elNoches = $('#resNoches');
            if (elNoches) elNoches.textContent = cantNoches;

            const elGuest = $('#resGuest');
            if (elGuest) elGuest.textContent = texto;

            const elTxtHuespedes = $('#txtHuespedes');
            if (elTxtHuespedes) elTxtHuespedes.textContent = texto;

            const elCntHab = $('#cntHab');
            if (elCntHab) elCntHab.textContent = numHab;

            const elCntHue = $('#cntHue');
            if (elCntHue) elCntHue.textContent = numHue;

            const elCapHuespedes = $('#capHuespedes');
            if (elCapHuespedes) elCapHuespedes.textContent = numHue;

            // actualiza tarjeta de habitacion seleccionada
            if (selectTipo) {
                const opt = selectTipo.selectedOptions[0];
                if (opt) {
                    const nombre = opt.dataset.nombre || opt.textContent;
                    const precio = parseFloat(opt.dataset.precio || 0);
                    const cama = opt.dataset.cama || '';
                    const tamano = opt.dataset.tamano || '';
                    const img = opt.dataset.img || '';
                    const desc = opt.dataset.desc || '';

                    const habTitulo = $('#resHabTitulo');
                    if (habTitulo) habTitulo.textContent = nombre;

                    const habCama = $('#resHabCama');
                    if (habCama) habCama.textContent = cama;

                    const habTamano = $('#resHabTamano');
                    if (habTamano) habTamano.textContent = tamano ? tamano + ' m²' : '';

                    const habDesc = $('#resHabDesc');
                    if (habDesc) habDesc.textContent = desc;

                    const habImg = $('#resHabImg');
                    if (habImg && img) habImg.src = img;

                    const habPrecio = $('#resHabPrecio');
                    if (habPrecio) habPrecio.textContent = 'S/ ' + precio.toFixed(2);

                    // calculo estimado del total
                    let totalEstimado = precio * cantNoches * numHab;

                    // extras seleccionados
                    $$('.hw-extra-check:checked', form).forEach((chk) => {
                        const row = chk.closest('.hw-extra-item');
                        if (row) {
                            const pExtra = parseFloat(chk.dataset.precio || 0);
                            const cantInput = $('.hw-extra-cant', row);
                            const cExtra = parseInt(cantInput ? cantInput.value : 1, 10) || 1;
                            totalEstimado += pExtra * cExtra;
                        }
                    });

                    const elTotalEst = $('#resTotalEstimado');
                    if (elTotalEst) {
                        elTotalEst.textContent = 'S/ ' + totalEstimado.toFixed(2);
                    }
                }
            }
        };

        if (entrada && salida) {
            if (entrada.value) salida.min = sumarDias(entrada.value, 1);
            entrada.addEventListener('change', () => {
                if (!entrada.value) return;
                salida.min = sumarDias(entrada.value, 1);
                if (!salida.value || salida.value <= entrada.value) {
                    salida.value = sumarDias(entrada.value, 1);
                }
                const aviso = $('#avisoFechas');
                if (aviso) aviso.classList.add('d-none');
                pintar();
            });
            salida.addEventListener('change', pintar);
        }

        if (selectTipo) {
            selectTipo.addEventListener('change', pintar);
        }

        // cambio en servicios extras
        $$('.hw-extra-check', form).forEach((chk) => {
            chk.addEventListener('change', () => {
                const row = chk.closest('.hw-extra-item');
                if (row) {
                    const cantInput = $('.hw-extra-cant', row);
                    if (cantInput) {
                        cantInput.disabled = !chk.checked;
                        if (chk.checked && (!cantInput.value || cantInput.value <= 0)) {
                            cantInput.value = 1;
                        }
                    }
                }
                pintar();
            });
        });
        $$('.hw-extra-cant', form).forEach((c) => {
            c.addEventListener('input', pintar);
        });

        // steppers de habitaciones y huespedes
        $$('[data-paso]', form).forEach((boton) => {
            boton.addEventListener('click', () => {
                const [campo, delta] = boton.dataset.paso.split(':');
                const input = campo === 'hab' ? hab : hue;
                const max = campo === 'hab' ? 5 : 20;
                if (input) {
                    input.value = Math.min(max, Math.max(1, (parseInt(input.value, 10) || 1) + parseInt(delta, 10)));
                    pintar();
                }
            });
        });

        const btnAplicar = $('#btnAplicar');
        if (btnAplicar) {
            btnAplicar.addEventListener('click', () => {
                const btnHuespedes = $('#btnHuespedes');
                if (btnHuespedes) {
                    bootstrap.Dropdown.getOrCreateInstance(btnHuespedes).hide();
                }
            });
        }

        // codigo promocional
        const btnPromo = $('#btnPromo');
        const cajaPromo = $('#cajaPromo');
        const codigo = $('#codigoPromo');
        const cerrarPromo = $('#cerrarPromo');

        if (codigo && codigo.value && btnPromo && cajaPromo) {
            btnPromo.classList.add('d-none');
            cajaPromo.classList.remove('d-none');
        }
        if (btnPromo && cajaPromo && codigo) {
            btnPromo.addEventListener('click', () => {
                btnPromo.classList.add('d-none');
                cajaPromo.classList.remove('d-none');
                codigo.focus();
            });
        }
        if (cerrarPromo && codigo && cajaPromo && btnPromo) {
            cerrarPromo.addEventListener('click', () => {
                codigo.value = '';
                cajaPromo.classList.add('d-none');
                btnPromo.classList.remove('d-none');
            });
        }

        // modal de confirmacion con datos de huesped
        const elModalHuesped = $('#modalHuesped');
        if (elModalHuesped) {
            const modal = bootstrap.Modal.getOrCreateInstance(elModalHuesped);
            const btnReservar = $('#btnReservar');
            if (btnReservar) {
                btnReservar.addEventListener('click', () => {
                    if (noches() === 0) {
                        const aviso = $('#avisoFechas');
                        if (aviso) aviso.classList.remove('d-none');
                        if (salida) salida.focus();
                        return;
                    }
                    modal.show();
                });
            }
            if (form.dataset.abrirModal === 'true') {
                modal.show();
            }
        }

        pintar();
    }
})();
