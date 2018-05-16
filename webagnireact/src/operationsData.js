function createOperation(name, type, date, state, level, visibility) {
    return { name, type, date, state, level, visibility };
}

const operationsData = [
    {name: 'teste', type: 'limpeza de mato', date: '27/04/2018', state: 'nao tratado', level: 4, visibility: 'publico'},
    createOperation("Lixo no mato", "limpeza de mato", "27/04/2018", "nao tratado", 4, "publico"),
    createOperation("Mau acesso na zona", "zona de mau acesso", "27/04/2018", "nao tratado", 3, "publico"),
];

export default operationsData;