import jsonfile from 'jsonfile';

const enum filePaths {
    POTKI = "./data/potki.json"
}

function readJson(path: string) {
    return jsonfile.readFile(path);
}

function writeJson(path: string, data: any) {
    return jsonfile.writeFile(path, data)
}

export async function modifyPotka(id: string, name: string) {
    try {
        let file = await readJson(filePaths.POTKI);
        console.group("Modifying Potka")
        console.log(file)
        file[id] = name;
        console.log(file)
        console.groupEnd()
        await writeJson(filePaths.POTKI, file)
    } catch (e) {
        throw e
    }
}

export async function getPotki() {
    return readJson(filePaths.POTKI);
}