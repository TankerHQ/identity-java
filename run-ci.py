import argparse
import sys

import cli_ui as ui

import tankerci
import tankerci.git
import tankerci.gcp


def build_and_test() -> None:
    ui.info_1("Building everything")
    tankerci.run("./gradlew", "assemble")

    ui.info_1("Running tests")
    # In case you're wondering:
    # https://stackoverflow.com/questions/50104666/gradle-difference-between-test-and-check
    tankerci.run("./gradlew", "test")


def deploy(*, git_tag: str) -> None:
    version = tankerci.version_from_git_tag(git_tag)
    tankerci.bump_files(version)
    build_and_test()

    ui.info_1("Deploying Identity SDK to maven.tanker.io")
    tankerci.gcp.GcpProject("tanker-prod").auth()
    tankerci.run("./gradlew", "publish")


def main():
    parser = argparse.ArgumentParser()
    subparsers = parser.add_subparsers(title="subcommands", dest="command")

    subparsers.add_parser("build-and-test")

    deploy_parser = subparsers.add_parser("deploy")
    deploy_parser.add_argument("--git-tag", required=True)

    subparsers.add_parser("mirror")

    args = parser.parse_args()
    if args.command == "build-and-test":
        build_and_test()
    elif args.command == "deploy":
        deploy(git_tag=args.git_tag)
    elif args.command == "mirror":
        tankerci.git.mirror(github_url="git@github.com:TankerHQ/identity-java")
    else:
        parser.print_help()
        sys.exit(1)


if __name__ == "__main__":
    main()
