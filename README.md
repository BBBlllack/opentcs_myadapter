// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: CC-BY-4.0

# 基于OPENTCS的通信适配模块 - 毕设系统

> opentcs-mycommadapter 为主要工作

> 配合仓库[opentcs_python_client](https://github.com/BBBlllack/opentcs_python_client)使用(模拟器)

> 配合仓库[opentcs_s400_ros](https://github.com/BBBlllack/opentcs_s400_ros)使用(接入车辆)

# openTCS

![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)

- **Homepage**: [https://www.opentcs.org/](https://www.opentcs.org/)
- **Changelog**: [changelog.adoc](./opentcs-documentation/src/docs/release-notes/changelog.adoc)

---

**openTCS** (short for *open Transportation Control System*) is a free platform for controlling fleets of [automated guided vehicles (AGVs)](https://en.wikipedia.org/wiki/Automated_guided_vehicle) and mobile robots.
It should generally be possible to control any automatic vehicle with communication capabilities with it, but AGVs are the main target.

openTCS is maintained by the openTCS team at the [Fraunhofer Institute for Material Flow and Logistics](https://www.iml.fraunhofer.de/).

The software runs on **Java 21**, with the recommended Java distribution being the one provided by the [Adoptium project](https://adoptium.net/).
All libraries required for compiling and/or using it are freely available, too.

> ⚠️ openTCS itself is **not a complete product** you can use out-of-the-box to control AGVs with.
> It is a **framework** that provides basic data structures and algorithms (routing, dispatching, scheduling) needed for running an AGV system with multiple vehicles.

To use openTCS with real vehicles, you typically need to:
- Implement or integrate a **vehicle driver** (called *communication adapter* in openTCS terms) that translates between openTCS's abstract interface and your vehicle's protocol.
- Optionally, adapt algorithms or add project-specific strategies.

---

## Getting started

To get started with openTCS, please refer to the following:
- User's guide
- Developer's guide
- API documentation

These documents are included in the binary distribution and also available online on the [openTCS homepage](https://www.opentcs.org/).

---

## Licensing

openTCS is licensed under multiple licenses. Here's a brief summary (as of **November 2024**):

- All original source code: [MIT License](./LICENSES/MIT.txt)
- All original assets (including documentation): [CC-BY-4.0](./LICENSES/CC-BY-4.0.txt)
- Some configuration/data files: [CC0-1.0](./LICENSES/CC0-1.0.txt)
- Some third-party assets: [Apache-2.0](./LICENSES/Apache-2.0.txt), [OFL-1.1](./LICENSES/OFL-1.1.txt)

👉 For accurate license information, check individual files and the `REUSE.toml` file.

---

## Contributing

We welcome contributions!
Please see [CONTRIBUTING.adoc](./CONTRIBUTING.adoc) for contribution guidelines.
