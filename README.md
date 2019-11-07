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

TODO: Add information about building this project.

This project uses [Kohsuke's GitHub API Project](https://github.com/github-api/github-api) to interact with the 
GitHub API. This library expects your credentials (ie. OAUTH Token) in a `~/.github` directory.
More information [here](https://github-api.kohsuke.org/#Authentication).
