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
    <nav class="border-b border-gray-200 bg-white">
        <div class="mx-auto max-w-5xl px-4 py-4">
            <a href="/" class="text-xl font-medium text-gray-900">
                Анализатор страниц
            </a>
        </div>
    </nav>

    <main class="mx-auto max-w-5xl px-4 py-10">
        @if (flash != null)
            <div role="alert" class="mb-6 rounded border border-gray-200 bg-gray-50 p-4">
                ${flash}
            </div>
        @endif

        ${content}
    </main>
</body>
</html>