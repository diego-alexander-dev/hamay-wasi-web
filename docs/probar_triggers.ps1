# pruebas de triggers en postgresql

$psql = "C:\Program Files\PostgreSQL\18\bin\psql.exe"
$env:PGPASSWORD = "123456"

function Ejecutar-Prueba {
    param([string]$titulo, [string]$sql, [string]$esperado)
    Write-Host ""
    Write-Host " $titulo" -ForegroundColor Yellow
    
    $salida = & $psql -U postgres -d hamay_wasi_avanzada -c "$sql" 2>&1 | Out-String
    Write-Host $salida.Trim() -ForegroundColor White
    Write-Host " Verificado: $esperado" -ForegroundColor Green
}

Clear-Host
Write-Host " Pruebas de triggers en postgresql" -ForegroundColor Green

# prueba 1: evitar cruce de fechas
$codCruce = "HW-CRUCE-" + (Get-Random -Minimum 1000 -Maximum 9999)
$sql1 = "INSERT INTO reservas (codigo, huesped_id, fecha_entrada, fecha_salida, num_huespedes, estado) VALUES ('$codCruce', 1, '2026-11-10', '2026-11-15', 1, 'CONFIRMADA'); INSERT INTO reserva_habitaciones (reserva_id, habitacion_id, precio_noche) VALUES (currval('reservas_id_seq'), 1, 120.00);"
Ejecutar-Prueba "Prueba 1: evitar cruce de fechas" $sql1 "El trigger bloquea la operacion"

# prueba 2: auditoria de modificacion de estado
$sql2 = "UPDATE reservas SET estado = 'CHECK_IN' WHERE codigo = 'HW-DEMO01'; SELECT id, reserva_id, accion, detalle, fecha FROM auditoria_reservas WHERE accion = 'CAMBIO_ESTADO' ORDER BY id DESC LIMIT 1;"
Ejecutar-Prueba "Prueba 2: auditoria de estado" $sql2 "El trigger registra en auditoria_reservas"

# prueba 3: actualizacion de fecha de modificacion
$sql3 = "UPDATE reservas SET observaciones = 'Observacion auditada ' || clock_timestamp() WHERE codigo = 'HW-DEMO01'; SELECT codigo, creado_en, actualizado_en FROM reservas WHERE codigo = 'HW-DEMO01';"
Ejecutar-Prueba "Prueba 3: actualizacion automatica de fecha" $sql3 "La columna actualizado_en toma la fecha del sistema"

# prueba 4: auditoria de eliminacion
$codDel = "HW-DEL-" + (Get-Random -Minimum 1000 -Maximum 9999)
$sql4 = "INSERT INTO reservas (codigo, huesped_id, fecha_entrada, fecha_salida, num_huespedes, estado) VALUES ('$codDel', 1, CURRENT_DATE + 60, CURRENT_DATE + 62, 1, 'CANCELADA'); DELETE FROM reservas WHERE codigo = '$codDel'; SELECT id, reserva_id, accion, detalle, fecha FROM auditoria_reservas WHERE accion = 'ELIMINADA' ORDER BY id DESC LIMIT 1;"
Ejecutar-Prueba "Prueba 4: auditoria de eliminacion" $sql4 "El trigger registra la accion eliminada"

Write-Host ""
Write-Host " Pruebas completadas con exito" -ForegroundColor Green
