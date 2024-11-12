const canvas = document.getElementById('juliaCanvas');
const ctx = canvas.getContext('2d');

const cRealInput = document.getElementById('cReal');
const cRealValue = document.getElementById('cRealValue');

const cImagInput = document.getElementById('cImag');
const cImagValue = document.getElementById('cImagValue');

const iterationsInput = document.getElementById('iterations');
const iterationsValue = document.getElementById('iterationsValue');

const zoomInput = document.getElementById('zoom');
const zoomValue = document.getElementById('zoomValue');

const resetButton = document.getElementById('resetButton');

const cInput = document.getElementById('cInput');

let cRe = parseFloat(cRealInput.value);
let cIm = parseFloat(cImagInput.value);
let maxIterations = parseInt(iterationsInput.value);
let zoom = parseFloat(zoomInput.value);
let offsetX = 0;
let offsetY = 0;

function resizeCanvas() {
    canvas.width = window.innerWidth - document.getElementById('controls').offsetWidth;
    canvas.height = window.innerHeight;
    drawJuliaSet();
}

window.addEventListener('resize', resizeCanvas);

function drawJuliaSet() {
    const width = canvas.width;
    const height = canvas.height;

    const imageData = ctx.createImageData(width, height);
    const data = imageData.data;

    const minRe = -2.0 / zoom + offsetX;
    const maxRe = 2.0 / zoom + offsetX;
    const minIm = -2.0 / zoom + offsetY;
    const maxIm = minIm + (maxRe - minRe) * height / width;

    const reFactor = (maxRe - minRe) / (width - 1);
    const imFactor = (maxIm - minIm) / (height - 1);

    for (let y = 0; y < height; y++) {
        const zIm = maxIm - y * imFactor;
        for (let x = 0; x < width; x++) {
            const zRe = minRe + x * reFactor;

            let Z_re = zRe, Z_im = zIm;
            let n = 0;
            let isInside = true;

            for (; n < maxIterations; n++) {
                const Z_re2 = Z_re * Z_re;
                const Z_im2 = Z_im * Z_im;

                if (Z_re2 + Z_im2 > 4) {
                    isInside = false;
                    break;
                }

                Z_im = 2 * Z_re * Z_im + cIm;
                Z_re = Z_re2 - Z_im2 + cRe;
            }

            const p = (y * width + x) * 4;
            if (isInside) {
                data[p] = 0;
                data[p + 1] = 0;
                data[p + 2] = 0;
            } else {
                const t = n / maxIterations;
                const color = getColor(t);

                data[p] = color.r;
                data[p + 1] = color.g;
                data[p + 2] = color.b;
            }
            data[p + 3] = 255;
        }
    }

    ctx.putImageData(imageData, 0, 0);
}

function update() {
    cRe = parseFloat(cRealInput.value);
    cIm = parseFloat(cImagInput.value);
    maxIterations = parseInt(iterationsInput.value);
    zoom = parseFloat(zoomInput.value);

    cRealValue.textContent = cRe.toFixed(2);
    cImagValue.textContent = cIm.toFixed(2);
    iterationsValue.textContent = maxIterations;
    zoomValue.textContent = zoom.toFixed(1);

    drawJuliaSet();
}

cRealInput.addEventListener('input', update);
cImagInput.addEventListener('input', update);
iterationsInput.addEventListener('input', update);
zoomInput.addEventListener('input', update);

cInput.addEventListener('change', () => {
    const input = cInput.value.trim();
    const match = input.match(/^([-+]?[0-9]*\.?[0-9]+)\s*([+-])\s*([0-9]*\.?[0-9]+)i$/);
    if (match) {
        cRe = parseFloat(match[1]);
        cIm = parseFloat(match[3]);
        if (match[2] === '-') cIm = -cIm;

        cRealInput.value = cRe;
        cImagInput.value = cIm;
        cRealValue.textContent = cRe.toFixed(2);
        cImagValue.textContent = cIm.toFixed(2);

        drawJuliaSet();
    } else {
        alert('Некорректный формат. Пожалуйста, используйте формат: a + bi');
    }
});

resetButton.addEventListener('click', () => {
    offsetX = 0;
    offsetY = 0;
    zoom = 1.0;
    zoomInput.value = zoom;
    zoomValue.textContent = zoom.toFixed(1);
    drawJuliaSet();
});

function getColor(t) {
    const colors = [
        { r: 0, g: 7, b: 100 },
        { r: 32, g: 107, b: 203 },
        { r: 237, g: 255, b: 255 },
        { r: 255, g: 170, b: 0 },
        { r: 0, g: 2, b: 0 }
    ];

    const steps = colors.length - 1;
    const scaledT = t * steps;
    const idx1 = Math.floor(scaledT);
    const idx2 = Math.min(idx1 + 1, steps);
    const localT = scaledT - idx1;

    return interpolateColor(colors[idx1], colors[idx2], localT);
}

function interpolateColor(color1, color2, factor) {
    const result = {
        r: Math.round(color1.r + factor * (color2.r - color1.r)),
        g: Math.round(color1.g + factor * (color2.g - color1.g)),
        b: Math.round(color1.b + factor * (color2.b - color1.b))
    };
    return result;
}

let isDragging = false;
let dragStartX, dragStartY;

canvas.addEventListener('mousedown', function(e) {
    if (e.button === 0) {
        isDragging = true;
        dragStartX = e.clientX;
        dragStartY = e.clientY;
    }
});

canvas.addEventListener('mousemove', function(e) {
    if (isDragging) {
        const dx = e.clientX - dragStartX;
        const dy = e.clientY - dragStartY;

        const width = canvas.width;
        const height = canvas.height;
        const minRe = -2.0 / zoom;
        const maxRe = 2.0 / zoom;
        const minIm = -2.0 / zoom;
        const maxIm = minIm + (maxRe - minRe) * height / width;

        const reFactor = (maxRe - minRe) / (width - 1);
        const imFactor = (maxIm - minIm) / (height - 1);

        offsetX -= dx * reFactor;
        offsetY += dy * imFactor;

        dragStartX = e.clientX;
        dragStartY = e.clientY;

        drawJuliaSet();
    }
});

canvas.addEventListener('mouseup', function(e) {
    if (e.button === 0) {
        isDragging = false;
    }
});

canvas.addEventListener('mouseleave', function(e) {
    isDragging = false;
});

canvas.addEventListener('wheel', function(e) {
    e.preventDefault();

    const rect = canvas.getBoundingClientRect();
    const mouseX = e.clientX - rect.left;
    const mouseY = e.clientY - rect.top;

    const width = canvas.width;
    const height = canvas.height;
    const minRe = -2.0 / zoom + offsetX;
    const maxRe = 2.0 / zoom + offsetX;
    const minIm = -2.0 / zoom + offsetY;
    const maxIm = minIm + (maxRe - minRe) * height / width;

    const reFactor = (maxRe - minRe) / (width - 1);
    const imFactor = (maxIm - minIm) / (height - 1);

    const zRe = minRe + mouseX * reFactor;
    const zIm = maxIm - mouseY * imFactor;

    if (e.deltaY < 0) {
        zoom *= 1.1;
    } else {
        zoom /= 1.1;
    }

    if (zoom < 1) zoom = 1;
    if (zoom > 1000) zoom = 1000;

    zoomInput.value = zoom;
    zoomValue.textContent = zoom.toFixed(1);

    const newMinRe = -2.0 / zoom + offsetX;
    const newMaxRe = 2.0 / zoom + offsetX;
    const newMinIm = -2.0 / zoom + offsetY;
    const newMaxIm = newMinIm + (newMaxRe - newMinRe) * height / width;

    const newReFactor = (newMaxRe - newMinRe) / (width - 1);
    const newImFactor = (newMaxIm - newMinIm) / (height - 1);

    const newZRe = newMinRe + mouseX * newReFactor;
    const newZIm = newMaxIm - mouseY * newImFactor;

    offsetX += zRe - newZRe;
    offsetY += zIm - newZIm;

    drawJuliaSet();
});

resizeCanvas();
