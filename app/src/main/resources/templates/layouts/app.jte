@param gg.jte.Content content
@param String flash = null

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Анализатор страниц</title>
    <link rel="stylesheet" href="/app.css">
</head>
<body>
    <div class="container mx-auto px-4 py-6">

        @if (flash != null)
            <div class="border px-4 py-3 mb-4">
                ${flash}
            </div>
        @endif

        ${content}
    </div>
</body>
</html>