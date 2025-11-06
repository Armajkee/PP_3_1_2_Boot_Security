const usersTableBody = document.getElementById('usersTableBody');
const usersTableSection = document.getElementById('usersTableSection');
const addUserSection = document.getElementById('addUserSection');
const usersBtn = document.getElementById('usersTableBtn');
const newUserBtn = document.getElementById('newUserBtn');

// Переключение между таблицей пользователей и формой добавления
usersBtn.addEventListener('click', () => {
    usersBtn.classList.add('active');
    newUserBtn.classList.remove('active');
    usersTableSection.classList.replace('hidden','visible');
    addUserSection.classList.replace('visible','hidden');
    fetchUsers();
});

newUserBtn.addEventListener('click', () => {
    newUserBtn.classList.add('active');
    usersBtn.classList.remove('active');
    addUserSection.classList.replace('hidden','visible');
    usersTableSection.classList.replace('visible','hidden');
    fetchRolesForAdd();
});

// Получение всех пользователей
async function fetchUsers() {
    try {
        const res = await fetch('/api/admin/users');
        if (!res.ok) throw new Error('Failed to fetch users');
        const users = await res.json();
        usersTableBody.innerHTML = '';
        users.forEach(u => {
            const tr = document.createElement('tr');
            const roles = u.roles.map(r => r.name.replace('ROLE_','')).join(', ');
            tr.innerHTML = `
                <td>${u.id}</td>
                <td>${u.name}</td>
                <td>${u.surname}</td>
                <td>${u.age}</td>
                <td>${u.email}</td>
                <td>${roles}</td>
                <td><button class="btn btn-info btn-sm" data-bs-toggle="modal" data-bs-target="#editUserModal" data-id="${u.id}">Edit</button></td>
                <td><button class="btn btn-danger btn-sm" data-bs-toggle="modal" data-bs-target="#deleteUserModal" data-id="${u.id}">Delete</button></td>
            `;
            usersTableBody.appendChild(tr);
        });
    } catch(e) { console.error(e); }
}

// Получение всех ролей для формы добавления
async function fetchRolesForAdd() {
    try {
        const res = await fetch('/api/admin/roles');
        if(!res.ok) throw new Error('Failed to fetch roles');
        const roles = await res.json();
        const select = document.getElementById('add-roles');
        select.innerHTML = '';
        roles.forEach(r => {
            const opt = document.createElement('option');
            opt.value = r.id;
            opt.textContent = r.name.replace('ROLE_','');
            select.appendChild(opt);
        });
    } catch(e){ console.error(e); }
}

// Добавление нового пользователя
document.getElementById('addUserForm').addEventListener('submit', async e => {
    e.preventDefault();
    const form = e.target;

    const data = Object.fromEntries(new FormData(form));
    const roles = Array.from(form.roles.selectedOptions).map(opt => parseInt(opt.value));
    const userDTO = {...data, age: parseInt(data.age), roles: roles};

    try {
        const res = await fetch('/api/admin/users', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(userDTO)
        });
        if (!res.ok) throw new Error('Ошибка при добавлении пользователя');

        form.reset();
        usersBtn.click();
    } catch(e) {
        console.error(e);
        alert('Не удалось добавить пользователя. Проверьте консоль.');
    }
});

// Edit User
const editModal = document.getElementById('editUserModal');
editModal.addEventListener('show.bs.modal', async e => {
    const id = e.relatedTarget.getAttribute('data-id');
    try {
        const res = await fetch('/api/admin/users/' + id);
        const u = await res.json();
        document.getElementById('edit-id').value = u.id;
        document.getElementById('edit-name').value = u.name;
        document.getElementById('edit-surname').value = u.surname;
        document.getElementById('edit-age').value = u.age;
        document.getElementById('edit-email').value = u.email;

        const rolesRes = await fetch('/api/admin/roles');
        const allRoles = await rolesRes.json();
        const select = document.getElementById('edit-roles');
        select.innerHTML = '';
        allRoles.forEach(r => {
            const opt = document.createElement('option');
            opt.value = r.id;
            opt.textContent = r.name.replace('ROLE_','');
            if(u.roles.some(ur => ur.id === r.id)) opt.selected = true;
            select.appendChild(opt);
        });
    } catch(e) { console.error(e); }
});

document.getElementById('editUserForm').addEventListener('submit', async e => {
    e.preventDefault();
    const id = document.getElementById('edit-id').value;

    const data = {
        name: document.getElementById('edit-name').value,
        surname: document.getElementById('edit-surname').value,
        age: parseInt(document.getElementById('edit-age').value),
        email: document.getElementById('edit-email').value,
        password: document.getElementById('edit-password').value
    };

    const roles = Array.from(document.getElementById('edit-roles').selectedOptions).map(opt => parseInt(opt.value));
    const userDTO = {...data, roles};

    try {
        const res = await fetch('/api/admin/users/' + id, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(userDTO)
        });

        if (!res.ok) throw new Error('Ошибка при редактировании пользователя');

        bootstrap.Modal.getInstance(editModal).hide();
        fetchUsers(); // обновляем таблицу
    } catch(e) {
        console.error(e);
        alert('Не удалось обновить пользователя. Проверьте консоль.');
    }
});

// Delete User
const deleteModal = document.getElementById('deleteUserModal');
deleteModal.addEventListener('show.bs.modal', async e => {
    const id = e.relatedTarget.getAttribute('data-id');
    const res = await fetch('/api/admin/users/' + id);
    const u = await res.json();
    document.getElementById('delete-id').value = u.id;
    document.getElementById('delete-name').value = u.name;
    document.getElementById('delete-surname').value = u.surname;
    document.getElementById('delete-age').value = u.age;
    document.getElementById('delete-email').value = u.email;
    const select = document.getElementById('delete-roles');
    select.innerHTML = '';
    u.roles.forEach(r => {
        const opt = document.createElement('option');
        opt.textContent = r.name.replace('ROLE_','');
        select.appendChild(opt);
    });
});

document.getElementById('deleteUserForm').addEventListener('submit', async e => {
    e.preventDefault();
    const id = document.getElementById('delete-id').value;
    try {
        await fetch('/api/admin/users/' + id, {method:'DELETE'});
        bootstrap.Modal.getInstance(deleteModal).hide();
        fetchUsers();
    } catch(e) { console.error(e); }
});

// Инициализация
fetchUsers();
