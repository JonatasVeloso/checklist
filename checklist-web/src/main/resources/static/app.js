const API_BASE_URL = "";

let categories = [];
let currentCategoryId = null;
let currentView = "today";

document.addEventListener("DOMContentLoaded", async () => {
    await loadCategories();
    await loadTodayTasks();
});

async function loadCategories() {
    const response = await fetch(`${API_BASE_URL}/api/categories`);

    if (!response.ok) {
        alert("Erro ao buscar categorias.");
        return;
    }

    categories = await response.json();

    renderCategoryTabs();
    renderCategoryOptions();
}

function renderCategoryTabs() {
    const container = document.getElementById("categoryTabs");
    container.innerHTML = "";

    categories.forEach(category => {
        const button = document.createElement("button");
        button.className = "tab";

        if (category.id === currentCategoryId) {
            button.classList.add("active");
        }

        button.textContent = category.name;
        button.onclick = () => loadTasksByCategory(category.id, category.name);

        container.appendChild(button);
    });
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

async function loadTodayTasks() {
    currentView = "today";
    currentCategoryId = null;

    document.getElementById("taskTitle").textContent = "Hoje";
    document.getElementById("taskSubtitle").textContent = "Tarefas que precisam de atenção hoje.";

    renderCategoryTabs();

    const response = await fetch(`${API_BASE_URL}/api/tasks/today`);

    if (!response.ok) {
        alert("Erro ao buscar tarefas de hoje.");
        return;
    }

    const tasks = await response.json();
    renderTasks(tasks);
}

async function loadAllTasks() {
    currentView = "all";
    currentCategoryId = null;

    document.getElementById("taskTitle").textContent = "Todas as tarefas";
    document.getElementById("taskSubtitle").textContent = "Lista completa de tarefas cadastradas.";

    renderCategoryTabs();

    const response = await fetch(`${API_BASE_URL}/api/tasks`);

    if (!response.ok) {
        alert("Erro ao buscar tarefas.");
        return;
    }

    const tasks = await response.json();
    renderTasks(tasks);
}

async function loadTasksByCategory(categoryId, categoryName) {
    currentView = "category";
    currentCategoryId = categoryId;

    document.getElementById("taskTitle").textContent = categoryName;
    document.getElementById("taskSubtitle").textContent = "Tarefas desta categoria.";

    renderCategoryTabs();

    const response = await fetch(`${API_BASE_URL}/api/tasks/category/${categoryId}`);

    if (!response.ok) {
        alert("Erro ao buscar tarefas por categoria.");
        return;
    }

    const tasks = await response.json();
    renderTasks(tasks);
}

function renderTasks(tasks) {
    const container = document.getElementById("taskList");
    container.innerHTML = "";

    if (tasks.length === 0) {
        container.innerHTML = `<div class="empty-message">Nenhuma tarefa encontrada.</div>`;
        return;
    }

    tasks.forEach(task => {
        const card = document.createElement("article");
        card.className = "task-card";

        card.innerHTML = `
            <div class="task-card-header">
                <div>
                    <h3>${escapeHtml(task.description)}</h3>
                    <p>${escapeHtml(task.observation || "")}</p>
                </div>

                <span class="badge status-${task.status}">
                    ${translateStatus(task.status)}
                </span>
            </div>

            <div class="task-meta">
                <span class="badge type-badge">${translateType(task.type)}</span>
                <span class="badge type-badge">${escapeHtml(task.categoryName)}</span>
                ${task.referenceDate ? `<span class="badge type-badge">${formatDate(task.referenceDate)}</span>` : ""}
            </div>

            <div class="task-actions">
                <button onclick="changeTaskStatus(${task.id}, 'PENDING')">Pendente</button>
                <button onclick="changeTaskStatus(${task.id}, 'DOING')">Fazendo</button>
                <button onclick="changeTaskStatus(${task.id}, 'WAITING')">Aguardando</button>
                <button onclick="changeTaskStatus(${task.id}, 'DONE')">Concluir</button>
                <button class="secondary" onclick='openTaskFormForEdit(${JSON.stringify(task)})'>Editar</button>
                <button class="danger" onclick="deleteTask(${task.id})">Excluir</button>
            </div>
        `;

        container.appendChild(card);
    });
}

function openCategoryForm() {
    document.getElementById("categoryName").value = "";
    document.getElementById("categoryOrder").value = categories.length + 1;
    document.getElementById("categoryModal").classList.remove("hidden");
}

function closeCategoryForm() {
    document.getElementById("categoryModal").classList.add("hidden");
}

async function saveCategory() {
    const name = document.getElementById("categoryName").value.trim();
    const displayOrder = Number(document.getElementById("categoryOrder").value);

    if (!name) {
        alert("Informe o nome da categoria.");
        return;
    }

    const response = await fetch(`${API_BASE_URL}/api/categories`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            name,
            displayOrder
        })
    });

    if (!response.ok) {
        alert("Erro ao salvar categoria.");
        return;
    }

    closeCategoryForm();
    await loadCategories();
}

function openTaskForm() {
    if (categories.length === 0) {
        alert("Cadastre uma categoria antes de criar tarefas.");
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
        alert("Erro ao salvar tarefa.");
        return;
    }

    closeTaskForm();
    await reloadCurrentView();
}

async function changeTaskStatus(taskId, status) {
    const response = await fetch(`${API_BASE_URL}/api/tasks/${taskId}/status?status=${status}`, {
        method: "PATCH"
    });

    if (!response.ok) {
        alert("Erro ao alterar status.");
        return;
    }

    await reloadCurrentView();
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

    await reloadCurrentView();
}

async function reloadCurrentView() {
    if (currentView === "today") {
        await loadTodayTasks();
        return;
    }

    if (currentView === "all") {
        await loadAllTasks();
        return;
    }

    if (currentView === "category" && currentCategoryId) {
        const category = categories.find(item => item.id === currentCategoryId);
        await loadTasksByCategory(currentCategoryId, category ? category.name : "Categoria");
    }
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