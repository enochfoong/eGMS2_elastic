const API_BASE_URL = "/api/documents";

async function fetchFiles() {
  const response = await fetch(API_BASE_URL);
  return response.json();
}

async function fetchFile(id) {
  const response = await fetch(`${API_BASE_URL}/${id}`);
  return response.json();
}

async function saveFile(file, fileData) {
  const formData = new FormData();
  formData.append("file", fileData);
  formData.append("grant_no", file.grant_no);

  const response = await fetch(API_BASE_URL, {
    method: "POST",
    body: formData,
  });
  return response.json();
}

async function deleteFile(id) {
  await fetch(`${API_BASE_URL}/${id}`, { method: "DELETE" });
}

async function searchFiles(query) {
  const response = await fetch(`${API_BASE_URL}/search?query=${query}`);
  return response.json();
}

function renderFiles(files) {
  const fileList = document.getElementById("fileList");
  fileList.innerHTML = ""; // Clear existing rows

  files.forEach((file) => {
    const row = document.createElement("tr");

    // Populate the row's inner HTML
    row.innerHTML = `
        <td>${file.id}</td>
        <td>${file.source.grant_no}</td>
        <td>${file.source.attachment.date}</td>
        <td>${file.source.attachment.content_type}</td>
        <td>${file.source.attachment.author}</td>
        <td>${file.source.attachment.format}</td>
        <td>${file.source.attachment.modified}</td>
        <td>${file.source.attachment.language}</td>
        <td>${file.source.attachment.metadata_date}</td>
        <td>${file.source.attachment.title}</td>
        <td>${file.source.attachment.creator_tool}</td>
        <td>${file.source.attachment.content_length}</td>
        <td>
          <button class="btn btn-warning btn-sm me-2" onclick="editFile('${file.id}')">Edit</button>
          <button class="btn btn-danger btn-sm" onclick="removeFile('${file.id}')">Delete</button>
        </td>
      `;

    // Add click event listener to the row
    row.addEventListener("click", (event) => {
      // Optional: Prevent the event from triggering when clicking on buttons
      if (event.target.tagName === "BUTTON") return;

      // Store the file ID in sessionStorage
      sessionStorage.setItem("selectedDocId", file.id);

      // Navigate to details.html
      window.location.href = "details.html";
    });

    // Append the row to the table body
    fileList.appendChild(row);
  });
}

async function loadFiles() {
  const files = await fetchFiles();
  renderFiles(files);
}

document.getElementById("fileForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const grantNo = document.getElementById("grantNo").value;
  const fileUpload = document.getElementById("fileUpload").files[0];

  const file = { grant_no: grantNo };

  const response = await saveFile(file, fileUpload);
  renderFiles(response.data.body);
  document.getElementById("fileForm").reset();
});

document.getElementById("searchForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const query = document.getElementById("searchQuery").value;
  const files = await searchFiles(query);
  renderFiles(files);
});

document.getElementById("resetButton").addEventListener("click", async () => {
  document.getElementById("searchQuery").value = "";
  loadFiles();
});

async function editFile(id) {
  const file = await fetchFile(id);
  document.getElementById("grantNo").value = file.grant_no;
}

async function removeFile(id) {
  await deleteFile(id);
  loadFiles();
}

// Initial load
loadFiles();
