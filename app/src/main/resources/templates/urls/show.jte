@import hexlet.code.model.Url
@import hexlet.code.model.UrlCheck

@param Url url
@param java.util.List<UrlCheck> checks
@param String flash

@template.layouts.app(
    flash = flash,
    content = @`
        <h1 class="text-3xl font-bold mb-6">
            Сайт: ${url.getName()}
        </h1>

        <table data-test="url" class="table-auto w-full border-collapse mb-6">
            <tbody>
                <tr>
                    <td class="border px-4 py-2 font-bold">ID</td>
                    <td class="border px-4 py-2">
                        ${url.getId()}
                    </td>
                </tr>

                <tr>
                    <td class="border px-4 py-2 font-bold">Имя</td>
                    <td class="border px-4 py-2">
                        ${url.getName()}
                    </td>
                </tr>

                <tr>
                    <td class="border px-4 py-2 font-bold">
                        Дата создания
                    </td>
                    <td class="border px-4 py-2">
                        ${url.getCreatedAt().toString()}
                    </td>
                </tr>
            </tbody>
        </table>

        <form method="post"
              action="/urls/${url.getId()}/checks"
              class="mb-6">
            <input
                type="submit"
                value="Запустить проверку"
                class="cursor-pointer rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700"
            >
        </form>

        <h2 class="text-2xl font-bold mb-4">
            Проверки
        </h2>

        <table data-test="checks" class="table-auto w-full border-collapse">
            <thead>
                <tr>
                    <th class="border px-4 py-2">ID</th>
                    <th class="border px-4 py-2">Код ответа</th>
                    <th class="border px-4 py-2">h1</th>
                    <th class="border px-4 py-2">title</th>
                    <th class="border px-4 py-2">description</th>
                    <th class="border px-4 py-2">Дата создания</th>
                </tr>
            </thead>

            <tbody>
                @for (var check : checks)
                    <tr>
                        <td class="border px-4 py-2">
                            ${check.getId()}
                        </td>

                        <td class="border px-4 py-2">
                            ${check.getStatusCode()}
                        </td>

                        <td class="border px-4 py-2">
                            ${check.getH1() == null
                                ? ""
                                : (check.getH1().length() > 200
                                    ? check.getH1().substring(0, 200) + "..."
                                    : check.getH1())}
                        </td>

                        <td class="border px-4 py-2">
                            ${check.getTitle() == null
                                ? ""
                                : (check.getTitle().length() > 200
                                    ? check.getTitle().substring(0, 200) + "..."
                                    : check.getTitle())}
                        </td>

                        <td class="border px-4 py-2">
                            ${check.getDescription() == null
                                ? ""
                                : (check.getDescription().length() > 200
                                    ? check.getDescription().substring(0, 200) + "..."
                                    : check.getDescription())}
                        </td>

                        <td class="border px-4 py-2">
                            ${check.getCreatedAt().toString()}
                        </td>
                    </tr>
                @endfor
            </tbody>
        </table>
    `
)