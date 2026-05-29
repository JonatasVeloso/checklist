const API_BASE_URL = "";

let categories = [];
let currentCategoryId = null;
let currentView = "today";
let selectedPerson = null;
let selectedPersonLabel = null;
let selectedTaskActionId = null;
let currentStatusFilter = "ALL";

document.addEventListener("DOMContentLoaded", () => {
    showPersonScreen();
});

function showPersonScreen() {
    document.getElementById("personScreen").classList.remove("hidden");
    document.getElementById("appScreen").classList.add("hidden");
}

async function selectPerson(person, label) {
    selectedPerson = person;
    selectedPersonLabel = label;

    document.getElementById("selectedPersonLabel").textContent = `Pessoa selecionada: ${label}`;
    document.getElementById("personScreen").classList.add("hidden");
    document.getElementById("appScreen").classList.remove("hidden");

    currentCategoryId = null;
    currentView = "today";
    currentStatusFilter = "ALL";
    selectedTaskActionId = null;

    await loadCategories();

    updateViewFilterSelect();
    updateStatusFilterSelect();
    renderCategoryFilters();

    await loadTasksForCurrentFilters();
}

function backToPersonScreen() {
    selectedPerson = null;
    selectedPersonLabel = null;
    currentCategoryId = null;
    currentView = "today";
    currentStatusFilter = "ALL";
    selectedTaskActionId = null;
    categories = [];

    const categorySelect = document.getElementById("categoryFilterSelect");

    if (categorySelect) {
        categorySelect.innerHTML = `<option value="ALL">Todas categorias</option>`;
    }

    document.getElementById("taskList").innerHTML = "";

    showPersonScreen();
}

async function selectStatusFilter(status) {
    currentStatusFilter = status;
    selectedTaskActionId = null;

    updateStatusFilterSelect();
    await loadTasksForCurrentFilters();
}

function updateStatusFilterSelect() {
    const select = document.getElementById("statusFilterSelect");

    if (!select) {
        return;
    }

    select.value = currentStatusFilter;
}

function updateStatusFilterButtons() {
    const buttons = {
        ALL: document.getElementById("statusFilterAll"),
        PENDING: document.getElementById("statusFilterPending"),
        DOING: document.getElementById("statusFilterDoing"),
        WAITING: document.getElementById("statusFilterWaiting"),
        DONE: document.getElementById("statusFilterDone")
    };

    Object.entries(buttons).forEach(([status, button]) => {
        if (!button) {
            return;
        }

        button.className = status === currentStatusFilter
            ? "btn btn-primary"
            : "btn btn-outline-primary";
    });
}

async function loadCategories() {
    if (!selectedPerson) {
        return;
    }

    const response = await fetch(`${API_BASE_URL}/api/categories/person/${selectedPerson}`);

    if (!response.ok) {
        alert("Erro ao buscar categorias.");
        return;
    }

    categories = await response.json();

    renderCategoryFilters();
    renderCategoryOptions();
}

function renderCategoryFilters() {
    const select = document.getElementById("categoryFilterSelect");

    if (!select) {
        return;
    }

    select.innerHTML = "";

    const allOption = document.createElement("option");
    allOption.value = "ALL";
    allOption.textContent = "Todas categorias";
    select.appendChild(allOption);

    categories.forEach(category => {
        const option = document.createElement("option");
        option.value = String(category.id);
        option.textContent = category.name;

        select.appendChild(option);
    });

    select.value = currentCategoryId === null ? "ALL" : String(currentCategoryId);
}

async function selectViewFilter(view) {
    currentView = view;
    selectedTaskActionId = null;

    updateViewFilterSelect();
    await loadTasksForCurrentFilters();
}

function renderCategoryOptions() {
    const select = document.getElementById("taskCategory");
    select.innerHTML = "";

    categories.forEach(category => {
        const option = document.createElement("option");
        option.value = category.id;
        option.textContent = category.name;

        select.appendChild(option);
    });
}

async function selectCategoryFilter(categoryId) {
    currentCategoryId = categoryId;
    selectedTaskActionId = null;

    renderCategoryFilters();
    await loadTasksForCurrentFilters();
}

async function selectCategoryFilterFromSelect(value) {
    selectedTaskActionId = null;

    if (value === "ALL") {
        currentCategoryId = null;
    } else {
        currentCategoryId = Number(value);
    }

    renderCategoryFilters();
    await loadTasksForCurrentFilters();
}

async function loadTodayTasks() {
    currentView = "today";
    selectedTaskActionId = null;

    updateViewFilterSelect();
    await loadTasksForCurrentFilters();
}

async function loadAllTasks() {
    currentView = "all";
    selectedTaskActionId = null;

    updateViewFilterSelect();
    await loadTasksForCurrentFilters();
}

function updateViewFilterSelect() {
    const select = document.getElementById("viewFilterSelect");

    if (!select) {
        return;
    }

    select.value = currentView;
}

function updateViewFilterButtons() {
    const todayButton = document.getElementById("todayFilterButton");
    const allButton = document.getElementById("allFilterButton");

    if (currentView === "today") {
        todayButton.className = "btn btn-primary";
        allButton.className = "btn btn-outline-primary";
        return;
    }

    todayButton.className = "btn btn-outline-primary";
    allButton.className = "btn btn-primary";
}

async function loadTasksForCurrentFilters() {
    if (!selectedPerson) {
        return;
    }

    updateViewFilterSelect();
    updateStatusFilterSelect();
    renderCategoryFilters();

    const url = currentView === "today"
        ? `${API_BASE_URL}/api/tasks/today/person/${selectedPerson}`
        : `${API_BASE_URL}/api/tasks/person/${selectedPerson}`;

    const response = await fetch(url);

    if (!response.ok) {
        alert("Erro ao buscar tarefas.");
        return;
    }

    let tasks = await response.json();

    if (currentCategoryId !== null) {
        tasks = tasks.filter(task => task.categoryId === currentCategoryId);
    }

    if (currentStatusFilter !== "ALL") {
        tasks = tasks.filter(task => task.status === currentStatusFilter);
    }

    updateTaskHeader();
    renderTasks(tasks);
}

function updateTaskHeader() {
    const viewLabel = currentView === "today" ? "Hoje" : "Todas";

    let categoryLabel = "Todas categorias";

    if (currentCategoryId !== null) {
        const category = categories.find(item => item.id === currentCategoryId);
        categoryLabel = category ? category.name : "Categoria";
    }

    const statusLabel = translateStatusFilter(currentStatusFilter);

    document.getElementById("taskTitle").textContent = "Tarefas";
    document.getElementById("taskSubtitle").textContent =
        `Visualização: ${viewLabel} | Status: ${statusLabel} | Categoria: ${categoryLabel} | Pessoa: ${selectedPersonLabel}`;
}

function translateStatusFilter(status) {
    if (status === "ALL") {
        return "Todos";
    }

    return translateStatus(status);
}

function renderTasks(tasks) {
    const container = document.getElementById("taskList");
    container.innerHTML = "";

    if (tasks.length === 0) {
        container.innerHTML = `
            <div class="alert alert-secondary mb-0 text-center rounded-3">
                Nenhuma tarefa encontrada.
            </div>
        `;
        return;
    }

    const tableWrapper = document.createElement("div");
    tableWrapper.className = "table-responsive";

    tableWrapper.innerHTML = `
        <table class="table table-sm table-hover align-middle mb-0 task-table">
            <thead class="table-light">
                <tr>
                    <th>Descrição</th>
                    <th class="status-column">Status</th>
                </tr>
            </thead>

            <tbody>
                ${tasks.map(task => `
                    <tr class="task-row ${selectedTaskActionId === task.id ? "table-active" : ""}"
                        onclick="toggleTaskActions(${task.id})">

                        <td>
                            <div class="fw-semibold task-description-cell">
                                ${escapeHtml(task.description)}
                            </div>

                            ${task.observation ? `
                                <div class="text-secondary small task-observation-preview">
                                    ${escapeHtml(task.observation)}
                                </div>
                            ` : `
                                <div class="text-secondary small">
                                    Sem observação
                                </div>
                            `}
                        </td>

                        <td class="status-column">
                            <div>
                                <span class="badge ${getStatusBadgeClass(task.status)}">
                                    ${translateStatus(task.status)}
                                </span>
                            </div>

                            <div class="mt-1">
                                <span class="badge text-bg-light border">
                                    ${translateType(task.type)}
                                </span>
                            </div>
                        </td>
                    </tr>

                    ${selectedTaskActionId === task.id ? `
                        <tr class="task-detail-row">
                            <td colspan="2">
                                <div class="task-detail-card">

                                    <div class="row g-2 mb-3">
                                        <div class="col-12 col-md-6">
                                            <div class="detail-label">Categoria</div>
                                            <div class="detail-value">${escapeHtml(task.categoryName)}</div>
                                        </div>

                                        <div class="col-12 col-md-6">
                                            <div class="detail-label">Data de referência</div>
                                            <div class="detail-value">
                                                ${task.referenceDate ? formatDate(task.referenceDate) : "-"}
                                            </div>
                                        </div>

                                        <div class="col-12 col-md-6">
                                            <div class="detail-label">Status</div>
                                            <div class="detail-value">${translateStatus(task.status)}</div>
                                        </div>

                                        <div class="col-12 col-md-6">
                                            <div class="detail-label">Tipo</div>
                                            <div class="detail-value">${translateType(task.type)}</div>
                                        </div>

                                        <div class="col-12">
                                            <div class="detail-label">Observação</div>
                                            <div class="detail-value">
                                                ${task.observation ? escapeHtml(task.observation) : "Sem observação"}
                                            </div>
                                        </div>
                                    </div>

                                    <div class="d-grid d-md-flex gap-2 justify-content-md-end">
                                        <button class="btn btn-success btn-sm"
                                                onclick='event.stopPropagation(); markTaskAsDone(${task.id});'>
                                            Concluído
                                        </button>

                                        <button class="btn btn-outline-secondary btn-sm"
                                                onclick='event.stopPropagation(); openTaskFormForEdit(${JSON.stringify(task)});'>
                                            Editar
                                        </button>

                                        <button class="btn btn-outline-danger btn-sm"
                                                onclick="event.stopPropagation(); deleteTask(${task.id});">
                                            Excluir
                                        </button>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    ` : ""}
                `).join("")}
            </tbody>
        </table>
    `;

    container.appendChild(tableWrapper);
}

function toggleTaskActions(taskId) {
    if (selectedTaskActionId === taskId) {
        selectedTaskActionId = null;
    } else {
        selectedTaskActionId = taskId;
    }

    loadTasksForCurrentFilters();
}

async function markTaskAsDone(taskId) {
    const response = await fetch(`${API_BASE_URL}/api/tasks/${taskId}/status?status=DONE`, {
        method: "PATCH"
    });

    if (!response.ok) {
        const errorText = await response.text();

        alert(
            "Erro ao concluir tarefa.\n\n" +
            "Status HTTP: " + response.status + "\n\n" +
            "Resposta do servidor:\n" + errorText
        );

        return;
    }

    selectedTaskActionId = null;

    if (currentStatusFilter !== "ALL" && currentStatusFilter !== "DONE") {
        currentStatusFilter = "ALL";
    }

    updateStatusFilterSelect();

    await loadTasksForCurrentFilters();
}

function openCategoryForm() {
    if (!selectedPerson) {
        alert("Selecione uma pessoa primeiro.");
        return;
    }

    document.getElementById("categoryPerson").value = selectedPerson;
    document.getElementById("categoryName").value = "";
    document.getElementById("categoryOrder").value = categories.length + 1;
    document.getElementById("categoryModal").classList.remove("hidden");
}

function closeCategoryForm() {
    document.getElementById("categoryModal").classList.add("hidden");
}

async function saveCategory() {
    const person = document.getElementById("categoryPerson").value;
    const name = document.getElementById("categoryName").value.trim();
    const displayOrder = Number(document.getElementById("categoryOrder").value);

    if (!name) {
        alert("Informe o nome da categoria.");
        return;
    }

    const requestBody = {
        name,
        displayOrder,
        person
    };

    try {
        const response = await fetch(`${API_BASE_URL}/api/categories`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            const errorText = await response.text();

            console.error("Erro ao salvar categoria");
            console.error("Status HTTP:", response.status);
            console.error("Resposta:", errorText);
            console.error("Body enviado:", requestBody);

            alert(
                "Erro ao salvar categoria.\n\n" +
                "Status HTTP: " + response.status + "\n\n" +
                "Resposta do servidor:\n" + errorText
            );

            return;
        }

        closeCategoryForm();
        await loadCategories();
        await loadTasksForCurrentFilters();

    } catch (error) {
        console.error("Erro de conexão ao salvar categoria:", error);
        console.error("Body enviado:", requestBody);

        alert(
            "Erro de conexão ao salvar categoria.\n\n" +
            "Verifique se você está acessando pelo ngrok e se o Spring Boot está rodando."
        );
    }
}

function openTaskForm() {
    if (!selectedPerson) {
        alert("Selecione uma pessoa primeiro.");
        return;
    }

    if (categories.length === 0) {
        alert(`Cadastre uma categoria para ${selectedPersonLabel} antes de criar tarefas.`);
        return;
    }

    document.getElementById("taskModalTitle").textContent = "Nova tarefa";
    document.getElementById("taskId").value = "";
    document.getElementById("taskCategory").value = currentCategoryId || categories[0].id;
    document.getElementById("taskDescription").value = "";
    document.getElementById("taskObservation").value = "";
    document.getElementById("taskStatus").value = "PENDING";
    document.getElementById("taskType").value = "DIARIA";
    document.getElementById("taskReferenceDate").value = "";
    document.getElementById("taskOrder").value = "1";

    document.getElementById("taskModal").classList.remove("hidden");
}

function openTaskFormForEdit(task) {
    document.getElementById("taskModalTitle").textContent = "Editar tarefa";
    document.getElementById("taskId").value = task.id;
    document.getElementById("taskCategory").value = task.categoryId;
    document.getElementById("taskDescription").value = task.description;
    document.getElementById("taskObservation").value = task.observation || "";
    document.getElementById("taskStatus").value = task.status;
    document.getElementById("taskType").value = task.type;
    document.getElementById("taskReferenceDate").value = task.referenceDate || "";
    document.getElementById("taskOrder").value = task.displayOrder;

    document.getElementById("taskModal").classList.remove("hidden");
}

function closeTaskForm() {
    document.getElementById("taskModal").classList.add("hidden");
}

async function saveTask() {
    const id = document.getElementById("taskId").value;
    const categoryId = Number(document.getElementById("taskCategory").value);
    const description = document.getElementById("taskDescription").value.trim();
    const observation = document.getElementById("taskObservation").value.trim();
    const status = document.getElementById("taskStatus").value;
    const type = document.getElementById("taskType").value;
    const referenceDateValue = document.getElementById("taskReferenceDate").value;
    const displayOrder = Number(document.getElementById("taskOrder").value);

    if (!description) {
        alert("Informe a descrição da tarefa.");
        return;
    }

    if (requiresReferenceDate(type) && !referenceDateValue) {
        alert("Esse tipo de tarefa precisa de uma data de referência.");
        return;
    }

    const body = {
        categoryId,
        description,
        observation,
        status,
        type,
        referenceDate: referenceDateValue || null,
        displayOrder
    };

    const url = id
        ? `${API_BASE_URL}/api/tasks/${id}`
        : `${API_BASE_URL}/api/tasks`;

    const method = id ? "PUT" : "POST";

    const response = await fetch(url, {
        method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
    });

    if (!response.ok) {
        const errorText = await response.text();

        alert(
            "Erro ao salvar tarefa.\n\n" +
            "Status HTTP: " + response.status + "\n\n" +
            "Resposta do servidor:\n" + errorText
        );

        return;
    }

    closeTaskForm();
    await loadTasksForCurrentFilters();
}

async function deleteTask(taskId) {
    const confirmed = confirm("Deseja realmente excluir esta tarefa?");

    if (!confirmed) {
        return;
    }

    const response = await fetch(`${API_BASE_URL}/api/tasks/${taskId}`, {
        method: "DELETE"
    });

    if (!response.ok) {
        alert("Erro ao excluir tarefa.");
        return;
    }

    await loadTasksForCurrentFilters();
}

function getStatusBadgeClass(status) {
    const classes = {
        PENDING: "text-bg-danger",
        DOING: "text-bg-primary",
        WAITING: "text-bg-warning",
        DONE: "text-bg-success"
    };

    return classes[status] || "text-bg-secondary";
}

function requiresReferenceDate(type) {
    return type === "SEMANAL"
        || type === "MENSAL"
        || type === "PROGRAMADA";
}

function translateStatus(status) {
    const labels = {
        PENDING: "Pendente",
        DOING: "Fazendo",
        WAITING: "Aguardando",
        DONE: "Concluído"
    };

    return labels[status] || status;
}

function translateType(type) {
    const labels = {
        DIARIA: "Diária",
        SEMANAL: "Semanal",
        MENSAL: "Mensal",
        PROGRAMADA: "Programada"
    };

    return labels[type] || type;
}

function formatDate(date) {
    const parts = date.split("-");

    if (parts.length !== 3) {
        return date;
    }

    return `${parts[2]}/${parts[1]}/${parts[0]}`;
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}