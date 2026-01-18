// js/signup.js
import { api } from './utils.js';

document.getElementById('register-form').addEventListener('submit', async (e) => {
    e.preventDefault(); 

    const registerData = {
        username: document.getElementById('reg-username').value,
        email: document.getElementById('reg-email').value,
        password: document.getElementById('reg-password').value
    };

    try {
        const response = await api.post("/users/register", registerData);

        if (response.ok) {
            const resultText = await response.text();
            
            // Extract the user ID from the backend's string response
            const idMatch = resultText.match(/\d+/);
            if (idMatch) {
                localStorage.setItem('userId', idMatch[0]);
                alert("Account created! Redirecting to markets...");
                window.location.href = "markets.html"; 
            }
        } else {
            const errorData = await response.json().catch(() => ({ message: "Registration failed" }));
            alert("Error: " + errorData.message);
        }
    } catch (error) {
        alert("Could not reach the server.");
    }
});