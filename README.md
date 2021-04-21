# cnj-common-rest-jee

Provides common REST API classes to simplify implementation of JAX-RS based endpoints.

## Status
![Build status](https://drone.cloudtrain.aws.msgoat.eu/api/badges/msgoat/cnj-common-rest-jee/status.svg)

## Tracing inbound and outbound requests

Tracing of inbound and outbound requests is disabled by default. To enable tracing, simply set configuration key
`cloudtrain.common.rest.trace.enabled` to __true__.

All traces are logged using logger `cloudtrain.common.rest.trace`, which is expected to be set to logging level __INFO__.

## Release information

Latest version is: `1.1.0`.

A changelog can be found in [changelog.md](changelog.md).
