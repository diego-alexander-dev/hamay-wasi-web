# prueba de concurrencia y sobreventa

$url = "https://hamay-wasi-web.onrender.com/reservas"

# rango de fechas
$diasFuturo = Get-Random -Minimum 30 -Maximum 300
$fechaBase = (Get-Date).AddDays($diasFuturo)
$entrada = $fechaBase.ToString("yyyy-MM-dd")
$salida = $fechaBase.AddDays(2).ToString("yyyy-MM-dd")
$tipoId = "1"

Write-Host "prueba de concurrencia (20 solicitudes)" -ForegroundColor Cyan
Write-Host "endpoint: $url" -ForegroundColor Cyan
Write-Host "fechas: $entrada al $salida | tipo: $tipoId" -ForegroundColor Cyan

$resultados = @()
$semillaDni = Get-Random -Minimum 1000 -Maximum 8000

1..20 | ForEach-Object {
    $i = $_
    $dni = ($semillaDni * 10000 + $i).ToString()
    $nombre = "Huesped_$i"
    $apellido = "PruebaQA"
    $email = "qa_prueba_${semillaDni}_${i}@hamaywasi.com"
    $tel = "%2B51987" + (100000 + $i)
    
    $postData = "numHabitaciones=1&numHuespedes=1&fechaEntrada=$entrada&fechaSalida=$salida&tipoHabitacionId=$tipoId&nombres=$nombre&apellidos=$apellido&tipoDocumento=DNI&numeroDocumento=$dni&email=$email&telefono=$tel&pais=Peru&observaciones=Prueba+Capitulo+5"

    $raw = curl.exe -s -o NUL -w "%{http_code}|%{time_total}|%{redirect_url}" -X POST $url -d $postData
    $parts = $raw.Split('|')
    $status = [int]$parts[0]
    $timeSec = [double]$parts[1]
    $timeMs = [math]::Round($timeSec * 1000)
    $redirect = if ($parts.Length -gt 2) { $parts[2] } else { "" }
    
    $codigo = if ($redirect -match "HW-[A-Z0-9]+") { $Matches[0] } else { "-" }
    
    $estado = ""
    $diagnostico = ""
    if ($status -eq 302) {
        $estado = "CONFIRMADA"
        $diagnostico = "reserva asignada (codigo: $codigo)"
    }
    elseif ($status -eq 200) {
        $estado = "RECHAZADA"
        $diagnostico = "aforo completo"
    }
    else {
        $estado = "ERROR $status"
        $diagnostico = "error en servidor"
    }

    $obj = [PSCustomObject]@{
        Peticion      = "#" + $i.ToString("D2")
        HTTP          = $status
        Tiempo        = "$timeMs ms"
        CodigoReserva = $codigo
        Resultado     = $estado
        Diagnostico   = $diagnostico
    }
    $resultados += $obj
    Write-Host ("[$($obj.Peticion)] HTTP $($obj.HTTP) | $($obj.Tiempo) | $($obj.Resultado) | $($obj.CodigoReserva)") -ForegroundColor $(if ($status -eq 302) { "Green" } elseif ($status -eq 200) { "Yellow" } else { "Red" })
}

Write-Host ""
Write-Host "resumen" -ForegroundColor Cyan
$resultados | Format-Table Peticion, HTTP, Tiempo, CodigoReserva, Resultado -AutoSize

$tot302 = ($resultados | Where-Object { $_.HTTP -eq 302 }).Count
$tot200 = ($resultados | Where-Object { $_.HTTP -eq 200 }).Count
$totErr = ($resultados | Where-Object { $_.HTTP -ge 400 -or $_.HTTP -eq 0 }).Count

Write-Host "metricas" -ForegroundColor Green
Write-Host "total peticiones : 20"
Write-Host "reservas exitosas: $tot302" -ForegroundColor Green
Write-Host "rechazadas aforo : $tot200" -ForegroundColor Yellow
Write-Host "errores          : $totErr" -ForegroundColor $(if ($totErr -eq 0) { "Green" } else { "Red" })
