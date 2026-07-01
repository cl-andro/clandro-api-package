# Clandro API

[![Build status](https://github.com/cl-andro/clandro-api/workflows/Build/badge.svg)](https://github.com/cl-andro/clandro-api/actions)
[![Join the chat at https://gitter.im/clandro/clandro](https://badges.gitter.im/clandro/clandro.svg)](https://gitter.im/clandro/clandro)

This is an app exposing Android API to command line usage and scripts or programs.

When developing or packaging, note that this app needs to be signed with the same
key as the main Clandro app for permissions to work (only the main Clandro app are
allowed to call the API methods in this app).

## Installation

Latest version is `v0.53.0`.

Clandro:API application can be obtained from [F-Droid](https://f-droid.org/en/packages/com.clandro.api/).

Additionally we provide per-commit debug builds for those who want to try
out the latest features or test their pull request. This build can be obtained
from one of the workflow runs listed on [Github Actions](https://github.com/cl-andro/clandro-api/actions/workflows/github_action_build.yml?query=branch%3Amaster+event%3Apush)
page.

Signature keys of all offered builds are different. Before you switch the
installation source, you will have to uninstall the Clandro application and
all currently installed plugins. Check https://github.com/cl-andro/clandro-app#Installation for more info.

## License

Released under the [GPLv3 license](http://www.gnu.org/licenses/gpl-3.0.en.html).

## How API calls are made through the clandro-api helper binary

The [clandro-api](https://github.com/cl-andro/clandro-api-package/blob/master/clandro-api.c)
client binary in the `clandro-api` package generates two linux anonymous namespace
sockets, and passes their address to the [ClandroApiReceiver broadcast receiver](https://github.com/cl-andro/clandro-api/blob/master/app/src/main/java/com/clandro/api/ClandroApiReceiver.java)
as in:

```
/system/bin/am broadcast ${BROADCAST_RECEIVER} --es socket_input ${INPUT_SOCKET} --es socket_output ${OUTPUT_SOCKET}
```

The two sockets are used to forward stdin from `clandro-api` to the relevant API
class and output from the API class to the stdout of `clandro-api`.

## Client scripts

Client scripts which processes command line arguments before calling the
`clandro-api` helper binary are available in the [clandro-api package](https://github.com/cl-andro/clandro-api-package).

## Ideas

- Wifi network search and connect.
- Add extra permissions to the app to (un)install apps, stop processes etc.
