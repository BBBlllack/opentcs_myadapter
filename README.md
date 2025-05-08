// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: CC-BY-4.0

# 基于OPENTCS的通信适配模块 - 毕设系统

> opentcs-mycommadapter 为主要工作

> 配合仓库[opentcs_python_client](https://github.com/BBBlllack/opentcs_python_client)使用

= openTCS

image:https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg[Contributor Covenant,link=CODE_OF_CONDUCT.md]

* Homepage: https://www.opentcs.org/
* Changelog: link:./opentcs-documentation/src/docs/release-notes/changelog.adoc[changelog.adoc]

openTCS (short for _open Transportation Control System_) is a free platform for controlling fleets of https://en.wikipedia.org/wiki/Automated_guided_vehicle[automated guided vehicles (AGVs)] and mobile robots.
It should generally be possible to control any automatic vehicle with communication capabilities with it, but AGVs are the main target.

openTCS is being maintained by the openTCS team at the https://www.iml.fraunhofer.de/[Fraunhofer Institute for Material Flow and Logistics].

The software runs on the Java platform version 21, with the recommended Java distribution being the one provided by the https://adoptium.net/[Adoptium project].
All libraries required for compiling and/or using it are freely available, too.

openTCS itself is not a complete product you can use out-of-the-box to control AGVs with.
Primarily, it is a framework/an implementation of the basic data structures and algorithms (routing, dispatching, scheduling) needed for running an AGV system with more than one vehicle.
It tries to be as generic as possible to allow interoperation with vehicles of practically any vendor.
Thus it is usually necessary to at least create and integrate a vehicle driver (called _communication adapter_ in openTCS-speak) that translates between the abstract interface of the openTCS kernel and the communication protocol your vehicle understands.
Depending on your needs, it might also be necessary to adapt algorithms or add project-specific strategies.

== Getting started

To get started with openTCS, please refer to the user's guide, the developer's guide and the API documentation.
These documents are included in the binary distribution and can also be read online on the https://www.opentcs.org/[openTCS homepage].

== Licensing

This work is licensed under multiple licences.
Because keeping this section up-to-date is challenging, here is a brief summary as of November 2024:

* All original source code is licensed under link:./LICENSES/MIT.txt[MIT].
* All original assets, including documentation, is licensed under link:./LICENSES/CC-BY-4.0.txt[CC-BY-4.0].
* Some configuration and data files are licensed under link:./LICENSES/CC0-1.0.txt[CC0-1.0].
* Some third-party assets are licensed under link:./LICENSES/Apache-2.0.txt[Apache-2.0] or link:./LICENSES/OFL-1.1.txt[OFL-1.1].

For more accurate information, check the individual files as well as the `REUSE.toml` files.

== Contributing

You are very welcome to contribute to this project.
Please see link:./CONTRIBUTING.adoc[CONTRIBUTING.adoc] for a few guidelines related to this.
