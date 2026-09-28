// ======================================================
// WASTEPICKUP FRONTEND
// ======================================================

// Spring Boot backend API base
const API_BASE = "http://localhost:8080/api";

// ======================================================
// PAGE NAVIGATION
// ======================================================

function showPage(pageId, button = null) {
    // Hide all pages
    document.querySelectorAll(".page").forEach(page => {
        page.classList.remove("active");
    });

    // Show selected page
    const page = document.getElementById(pageId);
    if (page) {
        page.classList.add("active");
    }

    // Update active nav button
    document.querySelectorAll(".nav-btn").forEach(btn => {
        btn.classList.remove("active");
    });

    if (button) {
        button.classList.add("active");
    } else {
        const matchingBtn = document.querySelector(`.nav-btn[onclick*="'${pageId}'"]`);
        if (matchingBtn) matchingBtn.classList.add("active");
    }

    // Load corresponding module data
    if (pageId === "dashboard") loadDashboard();
    if (pageId === "zones") loadZones();
    if (pageId === "schedules") loadSchedules();
    if (pageId === "households") loadHouseholds();
    if (pageId === "pickups") loadPickups();
    if (pageId === "reports") loadReports();

    // Scroll to top
    window.scrollTo({ top: 0, behavior: "smooth" });
}

// ======================================================
// SWAGGER NAVIGATION
// ======================================================

function openSwagger() {
    window.open("http://localhost:8080/swagger-ui/index.html", "_blank");
}

// ======================================================
// API HELPER
// ======================================================

async function apiRequest(url, options = {}) {
    try {
        const response = await fetch(API_BASE + url, {
            headers: {
                "Content-Type": "application/json",
                ...(options.headers || {})
            },
            ...options
        });

        if (!response.ok) {
            const errorText = await response.text();
            let errorMessage = `HTTP Error ${response.status}`;

            try {
                const parsed = JSON.parse(errorText);
                if (parsed.validationErrors && Object.keys(parsed.validationErrors).length > 0) {
                    errorMessage = Object.entries(parsed.validationErrors)
                        .map(([field, msg]) => `• ${field}: ${msg}`)
                        .join("\n");
                } else if (parsed.message) {
                    errorMessage = parsed.message;
                }
            } catch (e) {
                if (errorText) errorMessage = errorText;
            }

            throw new Error(errorMessage);
        }

        // Return null for empty response (e.g. 204 No Content)
        if (response.status === 204) {
            return null;
        }

        return await response.json();
    } catch (error) {
        console.error("API Error on " + url + ":", error);
        alert("Backend API Error:\n\n" + error.message);
        throw error;
    }
}

// ======================================================
// DASHBOARD
// ======================================================

async function loadDashboard() {
    try {
        const [zones, households, pickups] = await Promise.all([
            apiRequest("/zones").catch(() => []),
            apiRequest("/households").catch(() => []),
            apiRequest("/pickups").catch(() => [])
        ]);

        const zoneCountEl = document.getElementById("zoneCount");
        const householdCountEl = document.getElementById("householdCount");
        const pickupCountEl = document.getElementById("pickupCount");

        if (zoneCountEl) zoneCountEl.textContent = Array.isArray(zones) ? zones.length : 0;
        if (householdCountEl) householdCountEl.textContent = Array.isArray(households) ? households.length : 0;
        if (pickupCountEl) pickupCountEl.textContent = Array.isArray(pickups) ? pickups.length : 0;

        calculateAverage(pickups);
    } catch (error) {
        console.error("Dashboard error:", error);
    }
}

function calculateAverage(pickups) {
    const avgEl = document.getElementById("averageScore");
    if (!avgEl) return;

    if (!Array.isArray(pickups) || pickups.length === 0) {
        avgEl.textContent = "0%";
        return;
    }

    let total = 0;
    let count = 0;

    pickups.forEach(pickup => {
        const score = pickup.segregationScore ?? pickup.score;
        if (score !== undefined && score !== null) {
            total += Number(score);
            count++;
        }
    });

    const average = count === 0 ? 0 : total / count;
    avgEl.textContent = Math.round(average) + "%";
}

// ======================================================
// ZONES MODULE
// ======================================================

async function loadZones() {
    const container = document.getElementById("zonesTable");
    if (!container) return;

    container.innerHTML = '<div class="loading">Loading zones...</div>';

    try {
        const zones = await apiRequest("/zones");

        if (!zones || zones.length === 0) {
            container.innerHTML = '<div class="loading">No zones found. Add your first zone above!</div>';
            return;
        }

        let html = `
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Zone Name</th>
                        <th>Code</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
        `;

        zones.forEach(zone => {
            html += `
                <tr>
                    <td><strong>${zone.id ?? "-"}</strong></td>
                    <td>${zone.name ?? "-"}</td>
                    <td><span style="background:rgba(99,91,255,0.2);padding:4px 8px;border-radius:6px;font-weight:600;">${zone.code ?? "-"}</span></td>
                    <td>${zone.active ? '<span style="color:#34d399;">Active</span>' : '<span style="color:#9fa8ca;">Inactive</span>'}</td>
                </tr>
            `;
        });

        html += `</tbody></table>`;
        container.innerHTML = html;
    } catch (error) {
        container.innerHTML = '<div class="loading">Unable to load zones. Ensure Spring Boot is running.</div>';
    }
}

function showZoneForm() {
    const form = document.getElementById("zoneForm");
    if (form) form.classList.toggle("hidden");
}

async function addZone() {
    const nameInput = document.getElementById("zoneName");
    const codeInput = document.getElementById("zoneCode");
    const name = nameInput.value.trim();
    const code = codeInput.value.trim();

    if (!name || !code) {
        alert("Please enter both zone name and zone code.");
        return;
    }

    try {
        await apiRequest("/zones", {
            method: "POST",
            body: JSON.stringify({
                name: name,
                code: code,
                active: true
            })
        });

        alert("Zone added successfully!");
        nameInput.value = "";
        codeInput.value = "";
        showZoneForm();
        loadZones();
        loadDashboard();
    } catch (error) {
        console.error("Add zone error:", error);
    }
}

// ======================================================
// SCHEDULES MODULE
// ======================================================

async function loadSchedules() {
    const container = document.getElementById("schedulesTable");
    if (!container) return;

    container.innerHTML = '<div class="loading">Loading schedules...</div>';

    try {
        const schedules = await apiRequest("/schedules");

        if (!schedules || schedules.length === 0) {
            container.innerHTML = '<div class="loading">No pickup schedules found. Add one above!</div>';
            return;
        }

        let html = `
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Zone</th>
                        <th>Day</th>
                        <th>Start Time</th>
                        <th>End Time</th>
                    </tr>
                </thead>
                <tbody>
        `;

        schedules.forEach(schedule => {
            const zoneDisplay = schedule.zoneName 
                ? `${schedule.zoneName} (${schedule.zoneCode || "ID: " + schedule.zoneId})` 
                : (schedule.zoneId ?? "-");

            html += `
                <tr>
                    <td><strong>${schedule.id ?? "-"}</strong></td>
                    <td>${zoneDisplay}</td>
                    <td><strong>${schedule.pickupDay ?? schedule.day ?? "-"}</strong></td>
                    <td>${schedule.startTime ?? "-"}</td>
                    <td>${schedule.endTime ?? "-"}</td>
                </tr>
            `;
        });

        html += `</tbody></table>`;
        container.innerHTML = html;
    } catch (error) {
        container.innerHTML = '<div class="loading">Unable to load schedules.</div>';
    }
}

function showScheduleForm() {
    const form = document.getElementById("scheduleForm");
    if (form) form.classList.toggle("hidden");
}

async function addSchedule() {
    const zoneId = document.getElementById("scheduleZoneId").value.trim();
    const day = document.getElementById("scheduleDay").value;
    let startTime = document.getElementById("startTime").value;
    let endTime = document.getElementById("endTime").value;

    if (!zoneId || !day || !startTime || !endTime) {
        alert("Please fill all schedule fields.");
        return;
    }

    if (startTime.length === 5) startTime += ":00";
    if (endTime.length === 5) endTime += ":00";

    try {
        await apiRequest("/schedules", {
            method: "POST",
            body: JSON.stringify({
                zoneId: Number(zoneId),
                pickupDay: day,
                startTime: startTime,
                endTime: endTime
            })
        });

        alert("Schedule added successfully!");
        document.getElementById("scheduleZoneId").value = "";
        document.getElementById("scheduleDay").value = "";
        document.getElementById("startTime").value = "";
        document.getElementById("endTime").value = "";
        showScheduleForm();
        loadSchedules();
    } catch (error) {
        console.error("Add schedule error:", error);
    }
}

// ======================================================
// HOUSEHOLDS MODULE
// ======================================================

async function loadHouseholds() {
    const container = document.getElementById("householdsTable");
    if (!container) return;

    container.innerHTML = '<div class="loading">Loading households...</div>';

    try {
        const households = await apiRequest("/households");

        if (!households || households.length === 0) {
            container.innerHTML = '<div class="loading">No registered households found.</div>';
            return;
        }

        let html = `
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Resident Name</th>
                        <th>House Number</th>
                        <th>Phone</th>
                        <th>Zone</th>
                        <th>Avg Score</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
        `;

        households.forEach(household => {
            const zoneDisplay = household.zoneName 
                ? `${household.zoneName} (${household.zoneCode || "ID: " + household.zoneId})` 
                : (household.zoneId ?? "-");

            const statusDisplay = household.reminderFlagged
                ? `<span style="color:#f87171;font-weight:600;">⚠️ Flagged</span>`
                : `<span style="color:#34d399;font-weight:600;">✓ Good</span>`;

            html += `
                <tr>
                    <td><strong>${household.id ?? "-"}</strong></td>
                    <td>${household.residentName ?? household.name ?? "-"}</td>
                    <td>${household.houseNumber ?? household.address ?? "-"}</td>
                    <td>${household.phone ?? "-"}</td>
                    <td>${zoneDisplay}</td>
                    <td>${household.averageScore != null ? household.averageScore + "%" : "N/A"}</td>
                    <td>${statusDisplay}</td>
                </tr>
            `;
        });

        html += `</tbody></table>`;
        container.innerHTML = html;
    } catch (error) {
        container.innerHTML = '<div class="loading">Unable to load households.</div>';
    }
}

function showHouseholdForm() {
    const form = document.getElementById("householdForm");
    if (form) form.classList.toggle("hidden");
}

async function addHousehold() {
    const name = document.getElementById("householdName").value.trim();
    const address = document.getElementById("householdAddress").value.trim();
    const phone = document.getElementById("householdPhone")?.value.trim() || "";
    const zoneId = document.getElementById("householdZoneId").value.trim();

    if (!name || !address || !zoneId) {
        alert("Please enter Resident Name, House Number, and Zone ID.");
        return;
    }

    try {
        await apiRequest("/households", {
            method: "POST",
            body: JSON.stringify({
                residentName: name,
                houseNumber: address,
                phone: phone || null,
                zoneId: Number(zoneId)
            })
        });

        alert("Household added successfully!");
        document.getElementById("householdName").value = "";
        document.getElementById("householdAddress").value = "";
        if (document.getElementById("householdPhone")) document.getElementById("householdPhone").value = "";
        document.getElementById("householdZoneId").value = "";
        showHouseholdForm();
        loadHouseholds();
        loadDashboard();
    } catch (error) {
        console.error("Add household error:", error);
    }
}

// ======================================================
// PICKUP LOGS MODULE
// ======================================================

async function loadPickups() {
    const container = document.getElementById("pickupsTable");
    if (!container) return;

    container.innerHTML = '<div class="loading">Loading pickup logs...</div>';

    try {
        const pickups = await apiRequest("/pickups");

        if (!pickups || pickups.length === 0) {
            container.innerHTML = '<div class="loading">No pickup logs recorded yet.</div>';
            return;
        }

        let html = `
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Household</th>
                        <th>Zone</th>
                        <th>Collector</th>
                        <th>Score</th>
                        <th>Rolling Avg</th>
                        <th>Pickup Date & Time</th>
                    </tr>
                </thead>
                <tbody>
        `;

        pickups.forEach(pickup => {
            const score = pickup.segregationScore ?? pickup.score ?? 0;
            const householdDisplay = pickup.residentName
                ? `${pickup.residentName} (${pickup.houseNumber})`
                : `Household #${pickup.householdId}`;

            const formattedDate = pickup.pickupTime
                ? new Date(pickup.pickupTime).toLocaleString()
                : (pickup.pickupDate ?? "-");

            const flagTag = pickup.flaggedAfterPickup
                ? `<span style="color:#f87171;font-size:11px;margin-left:4px;">⚠️ Flagged</span>`
                : "";

            html += `
                <tr>
                    <td><strong>${pickup.id ?? "-"}</strong></td>
                    <td>${householdDisplay}</td>
                    <td>${pickup.zoneName ?? "-"}</td>
                    <td>${pickup.collectorName ?? "-"}</td>
                    <td><strong style="color:${score >= 60 ? '#34d399' : '#f87171'}">${score}%</strong></td>
                    <td>${pickup.rollingAverage != null ? pickup.rollingAverage + "%" : "-"} ${flagTag}</td>
                    <td>${formattedDate}</td>
                </tr>
            `;
        });

        html += `</tbody></table>`;
        container.innerHTML = html;
    } catch (error) {
        container.innerHTML = '<div class="loading">Unable to load pickup logs.</div>';
    }
}

function showPickupForm() {
    const form = document.getElementById("pickupForm");
    if (form) form.classList.toggle("hidden");
}

async function addPickup() {
    const householdId = document.getElementById("pickupHouseholdId").value.trim();
    const collector = document.getElementById("pickupCollector")?.value.trim() || "Collector 1";
    const score = document.getElementById("pickupScore").value.trim();
    const pickupTimeInput = document.getElementById("pickupTime")?.value;

    if (!householdId || score === "") {
        alert("Please enter Household ID and Segregation Score.");
        return;
    }

    const numScore = Number(score);
    if (numScore < 0 || numScore > 100) {
        alert("Segregation Score must be between 0 and 100.");
        return;
    }

    const payload = {
        householdId: Number(householdId),
        collectorName: collector,
        segregationScore: numScore
    };

    if (pickupTimeInput) {
        payload.pickupTime = pickupTimeInput;
    }

    try {
        await apiRequest("/pickups", {
            method: "POST",
            body: JSON.stringify(payload)
        });

        alert("Pickup recorded successfully!");
        document.getElementById("pickupHouseholdId").value = "";
        if (document.getElementById("pickupCollector")) document.getElementById("pickupCollector").value = "";
        document.getElementById("pickupScore").value = "";
        if (document.getElementById("pickupTime")) document.getElementById("pickupTime").value = "";
        showPickupForm();
        loadPickups();
        loadDashboard();
    } catch (error) {
        console.error("Add pickup error:", error);
    }
}

// ======================================================
// REPORTS MODULE
// ======================================================

async function loadReports() {
    const container = document.getElementById("reportsTable");
    if (!container) return;

    container.innerHTML = '<div class="loading">Generating report...</div>';

    try {
        const reports = await apiRequest("/pickups/reports/zone-scores");

        if (!reports || reports.length === 0) {
            container.innerHTML = '<div class="loading">No report data available. Ensure zones and pickups exist.</div>';
            return;
        }

        let html = `
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Zone Name</th>
                        <th>Zone Code</th>
                        <th>Average Score</th>
                        <th>Registered Households</th>
                        <th>Low Compliance Flagged</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
        `;

        reports.forEach(report => {
            const score = report.averageScore ?? 0;
            let statusBadge = '<span style="color:#f87171;font-weight:600;">Needs Improvement</span>';

            if (score >= 80) {
                statusBadge = '<span style="color:#34d399;font-weight:600;">Excellent</span>';
            } else if (score >= 60) {
                statusBadge = '<span style="color:#60a5fa;font-weight:600;">Good</span>';
            }

            html += `
                <tr>
                    <td><strong>${report.zoneName ?? "-"}</strong></td>
                    <td><span style="background:rgba(99,91,255,0.2);padding:4px 8px;border-radius:6px;font-weight:600;">${report.zoneCode ?? "-"}</span></td>
                    <td><strong>${Math.round(score)}%</strong></td>
                    <td>${report.householdCount ?? 0}</td>
                    <td>${report.flaggedHouseholdCount > 0 ? `<span style="color:#f87171;font-weight:bold;">${report.flaggedHouseholdCount}</span>` : 0}</td>
                    <td>${statusBadge}</td>
                </tr>
            `;
        });

        html += `</tbody></table>`;
        container.innerHTML = html;
    } catch (error) {
        container.innerHTML = '<div class="loading">Failed to load reports.</div>';
    }
}

// ======================================================
// APPLICATION INITIALIZATION
// ======================================================

document.addEventListener("DOMContentLoaded", () => {
    loadDashboard();
});