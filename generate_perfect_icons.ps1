Add-Type -AssemblyName System.Drawing

$srcLogoPath = "d:\projects\app\app\src\main\res\drawable\logo_game_boost.png"
$src = [System.Drawing.Bitmap]::FromFile($srcLogoPath)

# Bounding box is X=412..610 (W=198), Y=128..442 (H=314).
# Center of emblem:
$centerX = 412 + [int](198 / 2) # 511
$centerY = 128 + [int](314 / 2) # 285

# A square crop around the emblem:
# To give balanced margins around the 314px high emblem:
$boxSize = 440
$cropX = [int]($centerX - ($boxSize / 2)) # 511 - 220 = 291
$cropY = [int]($centerY - ($boxSize / 2)) # 285 - 220 = 65

$emblemSquare = New-Object System.Drawing.Bitmap($boxSize, $boxSize)
$gSquare = [System.Drawing.Graphics]::FromImage($emblemSquare)
$gSquare.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$gSquare.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
$gSquare.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality

$srcRect = New-Object System.Drawing.Rectangle($cropX, $cropY, $boxSize, $boxSize)
$dstRect = New-Object System.Drawing.Rectangle(0, 0, $boxSize, $boxSize)
$gSquare.DrawImage($src, $dstRect, $srcRect, [System.Drawing.GraphicsUnit]::Pixel)
$gSquare.Dispose()

# Save standalone cropped square emblem drawable
$emblemSquare.Save("d:\projects\app\app\src\main\res\drawable\ic_game_boost_emblem.png", [System.Drawing.Imaging.ImageFormat]::Png)

# Update launcher mipmaps with properly framed emblem
$densities = @{
    "mipmap-mdpi" = 48
    "mipmap-hdpi" = 72
    "mipmap-xhdpi" = 96
    "mipmap-xxhdpi" = 144
    "mipmap-xxxhdpi" = 192
}

foreach ($pair in $densities.GetEnumerator()) {
    $dir = "d:\projects\app\app\src\main\res\" + $pair.Key
    $sz = $pair.Value

    # Square icon
    $targetBmp = New-Object System.Drawing.Bitmap($sz, $sz)
    $g = [System.Drawing.Graphics]::FromImage($targetBmp)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.DrawImage($emblemSquare, 0, 0, $sz, $sz)
    $g.Dispose()

    $targetBmp.Save("$dir\ic_launcher.png", [System.Drawing.Imaging.ImageFormat]::Png)
    $targetBmp.Dispose()

    # Round icon (apply circular clip)
    $roundBmp = New-Object System.Drawing.Bitmap($sz, $sz)
    $gRound = [System.Drawing.Graphics]::FromImage($roundBmp)
    $gRound.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $gRound.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $gRound.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality

    $path = New-Object System.Drawing.Drawing2D.GraphicsPath
    $path.AddEllipse(0, 0, $sz, $sz)
    $gRound.SetClip($path)
    $gRound.DrawImage($emblemSquare, 0, 0, $sz, $sz)
    $gRound.Dispose()

    $roundBmp.Save("$dir\ic_launcher_round.png", [System.Drawing.Imaging.ImageFormat]::Png)
    $roundBmp.Dispose()
}

# Adaptive foreground: 108dp. Safe zone is inner 66dp (~61%).
# We place emblem squarely in inner 240px out of 432px canvas on dark matching background:
$fgSize = 432
$fgBmp = New-Object System.Drawing.Bitmap($fgSize, $fgSize)
$gFg = [System.Drawing.Graphics]::FromImage($fgBmp)
$gFg.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$gFg.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality

$innerSz = 230
$off = [int](($fgSize - $innerSz) / 2)
$gFg.DrawImage($emblemSquare, $off, $off, $innerSz, $innerSz)
$gFg.Dispose()
$fgBmp.Save("d:\projects\app\app\src\main\res\drawable\ic_launcher_foreground.png", [System.Drawing.Imaging.ImageFormat]::Png)
$fgBmp.Dispose()

$emblemSquare.Dispose()
$src.Dispose()
Write-Output "Perfected icons generated successfully."
