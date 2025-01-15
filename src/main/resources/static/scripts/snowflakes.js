const container = document.getElementById('main');

for (let i = 0; i < 50; i++) {
    const element = document.createElement('div');
    element.classList.add('snowflake');
    container.appendChild(element);
}
