@import hexlet.code.model.Url
@import hexlet.code.model.UrlCheck

@param java.util.List<Url> urls
@param java.util.Map<Long, UrlCheck> latestChecks
@param String flash

@template.layouts.app(
    flash = flash,
    content = @`
        <h1 class="text-3xl font-bold mb-6">Сайты</h1>

        <table data-test="urls" class="table-auto w-full border-collapse">
            <thead>
                <tr>
                    <th class="border px-4 py-2">ID</th>
                    <th class="border px-4 py-2">Имя</th>
                    <th class="border px-4 py-2">Последняя проверка</th>
                    <th class="border px-4 py-2">Код ответа</th>
                </tr>
            </thead>

            <tbody>
                @for (var url : urls)
                    <tr>
                        <td class="border px-4 py-2">
                            ${url.getId()}
                        </td>

                        <td class="border px-4 py-2">
                            <a href="/urls/${url.getId()}" class="underline">
                                ${url.getName()}
                            </a>
                        </td>

                        <td class="border px-4 py-2">
                            @if (latestChecks.containsKey(url.getId()))
                                ${latestChecks.get(url.getId()).getCreatedAt().toString()}
                            @endif
                        </td>

                        <td class="border px-4 py-2">
                            @if (latestChecks.containsKey(url.getId()))
                                ${latestChecks.get(url.getId()).getStatusCode()}
                            @endif
                        </td>
                    </tr>
                @endfor
            </tbody>
        </table>
    `
)