import json
import glob
import os

account_id = os.environ.get("NEW_RELIC_ACCOUNT_ID")

# Tenta converter para inteiro se a variável existir
if account_id:
    try:
        account_id = int(account_id)
    except ValueError:
        pass

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
        
        # Se informou o Account ID, injeta em todas as queries NRQL dos widgets
        if account_id:
            for widget in page_data.get("widgets", []):
                raw_config = widget.get("rawConfiguration", {})
                nrql_queries = raw_config.get("nrqlQueries", [])
                for query_obj in nrql_queries:
                    query_obj["accountIds"] = [account_id]

        dashboard["pages"].append(page_data)

output_path = os.path.join(base_dir, "dashboard-compiled.json")
with open(output_path, "w", encoding="utf-8") as out:
    json.dump(dashboard, out, indent=2)

print("✅ Dashboard compilado com sucesso em:", output_path)
