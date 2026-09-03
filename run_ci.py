#!/usr/bin/env python3
import os
import sys
import subprocess
import shutil

def print_step(message):
    print(f"\n==================================================")
    print(f"🚀 {message}")
    print(f"==================================================")

def check_requirements():
    print_step("1/4 - Verificando pré-requisitos locais")
    
    # Check Python 3
    print(f"✔ Python versão: {sys.version.split()[0]}")
    
    # Check Git
    if not shutil.which("git"):
        print("❌ Erro: 'git' não encontrado no PATH.")
        sys.exit(1)
    print("✔ Git instalado")

def build_dashboard():
    print_step("2/4 - Validando e compilando Dashboard localmente")
    
    build_script = os.path.join("dashboards", "order-platform", "build.py")
    
    if os.path.exists(build_script):
        result = subprocess.run([sys.executable, build_script], capture_output=False)
        if result.returncode != 0:
            print("❌ Erro ao compilar o dashboard localmente. Corrija os arquivos antes de enviar.")
            sys.exit(1)
        print("✔ Dashboard compilado com sucesso localmente!")
    else:
        print(f"⚠️ Script {build_script} não encontrado. Pulando etapa de build local.")

def git_commit_and_push():
    print_step("3/4 - Preparando envio para o Git")
    
    # Obtém mensagem de commit do usuário ou usa padrão
    commit_msg = input("Digite a mensagem do commit (ou Pressione Enter para usar a mensagem padrão): ").strip()
    if not commit_msg:
        commit_msg = "chore(observability): atualizando dashboards e pipeline de CI/CD"
        
    try:
        # Git Add
        print("\n-> Executando 'git add .'...")
        subprocess.run(["git", "add", "."], check=True)
        
        # Git Commit
        print(f"-> Executando 'git commit -m \"{commit_msg}\"'...")
        result = subprocess.run(["git", "commit", "-m", commit_msg], capture_output=True, text=True)
        
        if "nothing to commit" in result.stdout:
            print("ℹ️ Nenhuma alteração pendente para commit.")
        else:
            print(result.stdout.strip())
            
        # Descobre a branch atual
        branch_res = subprocess.run(["git", "rev-parse", "--abbrev-ref", "HEAD"], capture_output=True, text=True, check=True)
        current_branch = branch_res.stdout.strip()
        
        print_step(f"4/4 - Enviando alterações para a branch '{current_branch}'")
        
        # Git Push
        subprocess.run(["git", "push", "origin", current_branch], check=True)
        print(f"\n✅ Sucesso! Alterações enviadas. O GitHub Actions iniciará o pipeline automaticamente.")
        
    except subprocess.CalledProcessError as e:
        print(f"\n❌ Erro na execução dos comandos Git: {e}")
        sys.exit(1)

if __name__ == "__main__":
    check_requirements()
    build_dashboard()
    git_commit_and_push()
