import json
import glob
import os

dashboard = {
    "name": "Order Platform - Observability",
    "description": "Painel unificado de observabilidade da Order Platform gerado via CI/CD.",
    "permissions": "PUBLIC_READ_WRITE",
    "pages": []
}

base_dir = os.path.dirname(os.path.abspath(__file__))
page_files = sorted(glob.glob(os.path.join(base_dir, "pages", "*.json")))

for page_file in page_files:
    with open(page_file, "r", encoding="utf-8") as f:
        page_data = json.load(f)
        dashboard["pages"].append(page_data)

output_path = os.path.join(base_dir, "dashboard-compiled.json")
with open(output_path, "w", encoding="utf-8") as out:
    json.dump(dashboard, out, indent=2)

print("✅ Dashboard compilado com sucesso em:", output_path)
