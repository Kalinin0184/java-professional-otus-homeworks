const helloBtn = document.getElementById("helloBtn");
const helloOut = document.getElementById("helloOut");
const echoBtn = document.getElementById("echoBtn");
const echoInput = document.getElementById("echoInput");
const echoOut = document.getElementById("echoOut");

helloBtn.addEventListener("click", async () => {
    const response = await fetch("/api/hello");
    helloOut.textContent = await response.text();
});

echoBtn.addEventListener("click", async () => {
    const response = await fetch("/api/echo", {
        method: "POST",
        headers: { "Content-Type": "text/plain; charset=utf-8" },
        body: echoInput.value
    });
    echoOut.textContent = await response.text();
});
