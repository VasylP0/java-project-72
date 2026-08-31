@import hexlet.code.model.Url
@param Url url
@param String flash

@template.layouts.app(
    flash = flash,
    content = @`
        <h1 class="text-3xl font-bold mb-6">
            Сайт: ${url.getName()}
        </h1>

        <table data-test="url" class="table-auto w-full border-collapse">
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
    `
)