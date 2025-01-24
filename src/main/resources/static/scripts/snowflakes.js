const container = document.getElementsByTagName('body')[0];
const bodyHeight = document.body.scrollHeight;
const heightScaleFactor = document.body.scrollHeight / window.innerHeight;

for (let i = 0; i < 50 * heightScaleFactor; i++) {
    const element = document.createElement('div');
    element.classList.add('snowflake');

    const size = `${Math.random() * 0.8 + 0.4}vh`;
    element.style.setProperty('--size', size);

    const leftInitial = `${Math.random() * 50 - 25}vw`;
    const leftEnd = `${Math.random() * 50 - 25}vw`;
    element.style.setProperty('--left-ini', leftInitial);
    element.style.setProperty('--left-end', leftEnd);

    const initialPositionX = `${Math.random() * 100}%`
    element.style.left = initialPositionX;

    const duration = `${(Math.random() * 10 + 5) * heightScaleFactor}s`;
    const delay = `-${(Math.random() * 10) * heightScaleFactor}s`;
    element.style.animationDuration = duration;
    element.style.animationDelay = delay;

    const animationHeight = `${bodyHeight}px`;
    element.style.setProperty('--animation-height', animationHeight);

    if (i % 3 === 0) {
        element.style.filter = 'blur(1px)';
    }
    container.appendChild(element);
}
