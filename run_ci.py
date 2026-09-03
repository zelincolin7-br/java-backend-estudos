#!/usr/bin/env python3
import os
import sys
import subprocess
import shutil
import time

def print_step(message):
    print(f"\n==================================================")
    print(f"🚀 {message}")
    print(f"==================================================")

def check_requirements():
    print_step("1/5 - Verificando pré-requisitos locais")
    
    # Check Python 3
    print(f"✔ Python versão: {sys.version.split()[0]}")
    
    # Check Git
    if not shutil.which("git"):
        print("❌ Erro: 'git' não encontrado no PATH.")
        sys.exit(1)
    print("✔ Git instalado")

def ensure_runner_is_running():
    print_step("2/5 - Verificando GitHub Actions Runner Local")
    
    # Caminho padrão do runner na home do usuário
    home_dir = os.path.expanduser("~")
    runner_dir = os.path.join(home_dir, "actions-runner")
    runner_script = os.path.join(runner_dir, "run.sh")

    if not os.path.exists(runner_script):
        print(f"⚠️ Diretório do runner não encontrado em {runner_dir}. Pulando inicialização automática.")
        return

    # Verifica se o processo run.sh / Listener já está rodando
    try:
        ps_output = subprocess.check_output(["ps", "aux"], text=True)
        if "actions-runner" in ps_output and "run.sh" in ps_output:
            print("✔ GitHub Actions Runner já está em execução!")
            return
    except Exception:
        pass

    # Se não estiver rodando, inicia o runner em segundo plano
    print("⚙️ Runner local não detectado. Iniciando ~/actions-runner/run.sh em segundo plano...")
    try:
        # Inicia o processo desconectado do terminal atual
        subprocess.Popen(
            ["./run.sh"],
            cwd=runner_dir,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
            start_new_session=True
        )
        time.sleep(2)  # Aguarda 2 segundos para o processo se estabilizar
        print("✔ GitHub Actions Runner iniciado com sucesso!")
    except Exception as e:
        print(f"❌ Falha ao iniciar o runner local automaticamente: {e}")

def build_dashboard():
    print_step("3/5 - Validando e compilando Dashboard localmente")
    
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
    print_step("4/5 - Preparando envio para o Git")
    
    commit_msg = input("Digite a mensagem do commit (ou Pressione Enter para usar a mensagem padrão): ").strip()
    if not commit_msg:
        commit_msg = "feat(observability): setup modular dashboards and automatic ci/cd pipeline for new relic"
        
    try:
        print("\n-> Executando 'git add .'...")
        subprocess.run(["git", "add", "."], check=True)
        
        print(f"-> Executando 'git commit -m \"{commit_msg}\"'...")
        result = subprocess.run(["git", "commit", "-m", commit_msg], capture_output=True, text=True)
        
        if "nothing to commit" in result.stdout:
            print("ℹ️ Nenhuma alteração pendente para commit.")
        else:
            print(result.stdout.strip())
            
        branch_res = subprocess.run(["git", "rev-parse", "--abbrev-ref", "HEAD"], capture_output=True, text=True, check=True)
        current_branch = branch_res.stdout.strip()
        
        print_step(f"5/5 - Enviando alterações para a branch '{current_branch}'")
        
        subprocess.run(["git", "push", "origin", current_branch], check=True)
        print(f"\n✅ Sucesso! Alterações enviadas. O GitHub Actions runner local pegará o job automaticamente.")
        
    except subprocess.CalledProcessError as e:
        print(f"\n❌ Erro na execução dos comandos Git: {e}")
        sys.exit(1)

if __name__ == "__main__":
    check_requirements()
    ensure_runner_is_running()
    build_dashboard()
    git_commit_and_push()
