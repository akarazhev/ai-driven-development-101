const fs = require('fs');
const path = require('path');

const apiBase = process.env.NG_APP_API_BASE || 'http://localhost:8000';

const envFiles = [
  'src/environments/environment.prod.ts'
];

envFiles.forEach(file => {
  const filePath = path.join(__dirname, file);
  if (fs.existsSync(filePath)) {
    let content = fs.readFileSync(filePath, 'utf8');
    content = content.replace(
      /apiBase:\s*['"][^'"]*['"]/,
      `apiBase: '${apiBase}'`
    );
    fs.writeFileSync(filePath, content, 'utf8');
    console.log(`Updated ${file} with apiBase: ${apiBase}`);
  }
});
