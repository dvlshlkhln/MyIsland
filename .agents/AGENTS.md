# Project Rules for MyIsland

## Artifact Blueprint Preservation Rule
- Whenever creating or updating any artifact file (e.g. `implementation_plan.md`, `walkthrough.md`, system architecture, plan documents, design notes, or research summaries), ALWAYS save a copy of the artifact file into `project dev blueprint/` using a **unique, descriptive, feature-specific filename** (e.g. `project dev blueprint/implementation_plan_cutout_vertical_offset_fix.md`, `project dev blueprint/walkthrough_cutout_vertical_offset_fix.md`). NEVER overwrite previous blueprint files with generic names; each update/feature must have its own individually named file.

## README Auto-Update Rule
- Whenever new features, device presets, permissions, or system capabilities are implemented or updated in the project, ALWAYS update the `README.md` file in the root directory to accurately reflect the latest project features, setup steps, and device compatibility.

## Branch & Release Workflow Rule
- Use `working1` as the active development branch for updates, bug fixes, and feature additions.
- Only merge feature branches into `main` when ready to trigger the automatic GitHub Actions APK build workflow (`.github/workflows/android-build.yml`).
