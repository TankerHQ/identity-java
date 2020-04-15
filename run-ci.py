import argparse
import sys

import cli_ui as ui

import ci
import ci.gcp


def build_and_test() -> None:
    ui.info_1("Building everything")
    ci.run("./gradlew", "assemble")

    ui.info_1("Running tests")
    # In case you're wondering:
    # https://stackoverflow.com/questions/50104666/gradle-difference-between-test-and-check
    ci.run("./gradlew", "test")


def deploy(*, git_tag: str) -> None:
    version = ci.version_from_git_tag(git_tag)
    ci.bump_files(version)
    build_and_test()

    ui.info_1("Deploying Identity SDK to maven.tanker.io")
    ci.gcp.GcpProject("tanker-prod").auth()
    ci.run("./gradlew", "publish")


def main():
    parser = argparse.ArgumentParser()
    subparsers = parser.add_subparsers(title="subcommands", dest="command")

    check_parser = subparsers.add_parser("build-and-test")

    deploy_parser = subparsers.add_parser("deploy")
    deploy_parser.add_argument("--git-tag", required=True)

    args = parser.parse_args()
    if args.command == "build-and-test":
        build_and_test()
    elif args.command == "deploy":
        deploy(git_tag=args.git_tag)
    else:
        parser.print_help()
        sys.exit(1)


if __name__ == "__main__":
    main()
