const canvas = document.getElementById('mandelbrotCanvas');
const ctx = canvas.getContext('2d');

const iterationsInput = document.getElementById('iterations');
const iterationsValue = document.getElementById('iterationsValue');

const zoomInput = document.getElementById('zoom');
const zoomValue = document.getElementById('zoomValue');

const offsetXInput = document.getElementById('offsetX');
const offsetXValue = document.getElementById('offsetXValue');

const offsetYInput = document.getElementById('offsetY');
const offsetYValue = document.getElementById('offsetYValue');

const resetButton = document.getElementById('resetButton');

let maxIterations = parseInt(iterationsInput.value);
let zoom = parseFloat(zoomInput.value);
let offsetX = parseFloat(offsetXInput.value);
let offsetY = parseFloat(offsetYInput.value);

function resizeCanvas() {
    canvas.width = window.innerWidth - document.getElementById('controls').offsetWidth;
    canvas.height = window.innerHeight;
    drawMandelbrot();
}

window.addEventListener('resize', resizeCanvas);

function drawMandelbrot() {
    const width = canvas.width;
    const height = canvas.height;

    const imageData = ctx.createImageData(width, height);
    const data = imageData.data;

    const minRe = -2.0 / zoom + offsetX;
    const maxRe = 1.0 / zoom + offsetX;
    const minIm = -1.5 / zoom + offsetY;
    const maxIm = minIm + (maxRe - minRe) * height / width;

    const reFactor = (maxRe - minRe) / (width - 1);
    const imFactor = (maxIm - minIm) / (height - 1);

    for (let y = 0; y < height; y++) {
        const cIm = maxIm - y * imFactor;
        for (let x = 0; x < width; x++) {
            const cRe = minRe + x * reFactor;

            let Z_re = 0, Z_im = 0;
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
    maxIterations = parseInt(iterationsInput.value);
    zoom = parseFloat(zoomInput.value);
    offsetX = parseFloat(offsetXInput.value);
    offsetY = parseFloat(offsetYInput.value);

    iterationsValue.textContent = maxIterations;
    zoomValue.textContent = zoom.toFixed(1);
    offsetXValue.textContent = offsetX.toFixed(2);
    offsetYValue.textContent = offsetY.toFixed(2);

    drawMandelbrot();
}

iterationsInput.addEventListener('input', update);
zoomInput.addEventListener('input', update);
offsetXInput.addEventListener('input', update);
offsetYInput.addEventListener('input', update);

resetButton.addEventListener('click', () => {
    offsetX = 0;
    offsetY = 0;
    zoom = 1.0;

    offsetXInput.value = offsetX;
    offsetYInput.value = offsetY;
    zoomInput.value = zoom;

    offsetXValue.textContent = offsetX.toFixed(2);
    offsetYValue.textContent = offsetY.toFixed(2);
    zoomValue.textContent = zoom.toFixed(1);

    drawMandelbrot();
});

function getColor(t) {
    const colors = [
        { r: 0, g: 7, b: 100 },     // темно-синий
        { r: 32, g: 107, b: 203 },  // синий
        { r: 237, g: 255, b: 255 }, // белый
        { r: 255, g: 170, b: 0 },   // оранжевый
        { r: 0, g: 2, b: 0 }        // черный
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
        const maxRe = 1.0 / zoom;
        const minIm = -1.5 / zoom;
        const maxIm = minIm + (maxRe - minRe) * height / width;

        const reFactor = (maxRe - minRe) / (width - 1);
        const imFactor = (maxIm - minIm) / (height - 1);

        offsetX -= dx * reFactor;
        offsetY += dy * imFactor;

        dragStartX = e.clientX;
        dragStartY = e.clientY;

        offsetXInput.value = offsetX;
        offsetYInput.value = offsetY;
        offsetXValue.textContent = offsetX.toFixed(2);
        offsetYValue.textContent = offsetY.toFixed(2);

        drawMandelbrot();
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
    const maxRe = 1.0 / zoom + offsetX;
    const minIm = -1.5 / zoom + offsetY;
    const maxIm = minIm + (maxRe - minRe) * height / width;

    const reFactor = (maxRe - minRe) / (width - 1);
    const imFactor = (maxIm - minIm) / (height - 1);

    const cRe = minRe + mouseX * reFactor;
    const cIm = maxIm - mouseY * imFactor;

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
    const newMaxRe = 1.0 / zoom + offsetX;
    const newMinIm = -1.5 / zoom + offsetY;
    const newMaxIm = newMinIm + (newMaxRe - newMinRe) * height / width;

    const newReFactor = (newMaxRe - newMinRe) / (width - 1);
    const newImFactor = (newMaxIm - newMinIm) / (height - 1);

    const newCRe = newMinRe + mouseX * newReFactor;
    const newCIm = newMaxIm - mouseY * newImFactor;

    offsetX += cRe - newCRe;
    offsetY += cIm - newCIm;

    offsetXInput.value = offsetX;
    offsetYInput.value = offsetY;
    offsetXValue.textContent = offsetX.toFixed(2);
    offsetYValue.textContent = offsetY.toFixed(2);

    drawMandelbrot();
});

resizeCanvas();
