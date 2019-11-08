# Security Checklist Transformer

Utility application for taking the absolutely awesome Security checklists from [Sqreen](https://www.sqreen.com/),
converting the HTML into markdown and uploading them into your ticketing system.

Checklists Supported:
 - [SaaS CTO Security Checklist](https://www.sqreen.com/checklists/saas-cto-security-checklist)
 - [The Early Security Engineer’s First 90 Days Checklist](https://www.sqreen.com/checklists/security-engineer-checklist)

Others can be easily added as the HTML document structure for each of these checklists are pretty much the same.

## Why

When establishing a security plan for your company, you and your management will probably want to establish
both short and long term goals for your security team.

You may want to capture these "User Stories" in your companies tracking system.
The purpose of this tool is to simplify the import of these checklist items into your ticketing system
along with wiring each issue to the appropriate project board and adding the appropriate labels.

## What does it look like?

Currently this uploader only works for GitHub (the use case I needed to support) but others could easily be added.

![Security Roadmap Project Board](https://github.com/JLLeitschuh/security-checklist-transformer/raw/master/media/result-project.png)

## How do I use this project?

### Building

To build the application, checkout the repository and run:
```bash
./gradlew install
```

You can then execute the application from the root directory of this project with the following commands: 
```bash
./subprojects/security-checklist-application/build/install/security-checklist-application/bin/security-checklist-application -h
```

This will display the configuration options for how the various labels will be pre-processed before upload.
I've made the defaults the way that I wanted them, and most of the defaults will probably be fine for you as well.
Running without a `COMMAND` will allow you to preview what the uploaded content will somewhat look like before upload.

I also recommend running this against a throw-away repository before import into your production system.
 
```
Usage: checklisttransformer [OPTIONS] COMMAND [ARGS]...

Options:
  --list-names TEXT             The names of the lists to use. Default:
                                SaaS_CTO, SECURITY_ENGINEER
  --capitalize-phase            Capitalize the phase names. Default: true
  --capitalize-group            Capitalize the group names. Default: true
  --prepend-list-name-to-phase  Prepend the list name to the phase. Default:
                                true
  --various-semantic-fixes      Apply other semantic fixes to the data.
                                Default: true
  --quote-body-text             Quote body text and cite source in issue body.
                                Default: true
  -h, --help                    Show this message and exit

Commands:
  github  Upload to GitHub
```

When run with the `github` command there are additional options for uploading to GitHub

```
Usage: checklisttransformer github [OPTIONS]

  Upload to GitHub

Options:
  --repo-owner TEXT    The owner of the repository
  --repo-name TEXT     The name of the repository
  --project-name TEXT  The name of the project board
  -h, --help           Show this message and exit
```

### GitHub Authentication

This project uses [Kohsuke's GitHub API Project](https://github.com/github-api/github-api) to interact with the 
GitHub API. This library expects your credentials (ie. OAUTH Token) in a `~/.github` file.

More information [here](https://github-api.kohsuke.org/#Authentication).
