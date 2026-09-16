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

        <header class="mb-6 flex items-center gap-8">
            <a href="/" class="text-2xl font-semibold">
                Анализатор страниц
            </a>

            <nav>
                <a href="/urls" class="text-lg hover:underline">
                    Сайты
                </a>
            </nav>
        </header>

        @if (flash != null && !flash.isBlank())
            <div role="alert" class="border border-green-400 bg-green-100 text-green-700 px-4 py-3 mb-4 rounded">
                ${flash}
            </div>
        @endif

        ${content}
    </div>
</body>
</html>