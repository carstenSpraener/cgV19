# Setup for local development

## Cluster itself

* Install ubunut server version 22.0.4LTS or newer on at least 3 nodes
* install microk8s on each of this nodes:
  * `snap install microk8s --classic --channel=1.27`
* add the worker-nodes to the master by doing for each worker:
  * run `microk8s add-node` on the master
  * copy the command appropriate for the cluster network to the
    worker-node shell and execute it there
* if you have a local docker registry (unsecured) add this registry
    to the master by creating a directory:
  * `mkdir  -p /var/snap/microk8s/current/args/certs.d/teneriffa\:5080`
  * in this case the docker registry is on host teneriffa port 5080
  * add a host.toml to the directory like:
       ```bash
       root@lanzarote:~# cat /var/snap/microk8s/current/args/certs.d/teneriffa\:5080/hosts.toml
       server = "http://teneriffa:5080"

       [host."http://teneriffa:5080"]
       capabilities = ["pull", "resolve"]
       ```
* Enable ingress addon with command `microk8s enable ingress` on the master node
* Enable storage addon with command `microk8s enable hostpath-storage`

## Connecting the IDE / development computer with the cluster

On the master execute the command:
```bash
root@lanzarote:~# microk8s config
apiVersion: v1
clusters:
- cluster:
    certificate-authority-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUREekNDQWZlZ0F3SUJBZ0lVRkxuMndDbFRXYlFhUWp1Yjc4Z1h2RnR0ODJnd0RRWUpLb1pJaHZjTkFRRUwKQlFBd0Z6RVZNQk1HQTFVRUF3d01NVEF1TVRVeUxqRTRNeTR4TUI0WERUSXpNRGd4TkRFeU1EY3pPRm9YRFRNegpNRGd4TVRFeU1EY3pPRm93RnpFVk1CTUdBMVVFQXd3TU1UQXVNVFV5TGpFNE15NHhNSUlCSWpBTkJna3Foa2lHCjl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF5Wkc2cEhyRjNiaWFaOUlyWFBXVnNMTGlnb2Ezek50dzRUaUwKQ2U2L1lWeWh0Qm41RFNPLzZIVHBNdVR4WXp2U0pCMTdhdVh1WGkwcnBYcDFTQ2toWDMwb3dNWHZWL0RvcjI2awp1V2lNU3dBTjZ2ZTlzaWJ0emNXRmE0MmwrY3JVZVhGZmFCaXgyd05tNStSbHc5ak40Z1FwSDZFNWFmY3IzeVdqClVUanpsL0RlbFpLZld2TFFFL0R6ekpKdDZ2TXZ4cmNDb2RnVWdRdHZWVXE2Y0tsNC93bE80SXdoZjhxcDllVUIKcDNxTUZ6V2hMVlhGd08vZFdaQ3Z5Q1oxRjFCTWNESFBIa2Y0Y083cUEvZU9ZNlZqMWRsSUNGMHd0aE1LRmpidApDdUpHWS8xZ0wrZllNaGZuS01RYTVoZURPVC9pZHdLR1QxdHUrVlFjM1d6N3NaeWRTd0lEQVFBQm8xTXdVVEFkCkJnTlZIUTRFRmdRVXplVWdpZnpEcG5aZmgwS0ZtdjJXRjZnZW53OHdId1lEVlIwakJCZ3dGb0FVemVVZ2lmekQKcG5aZmgwS0ZtdjJXRjZnZW53OHdEd1lEVlIwVEFRSC9CQVV3QXdFQi96QU5CZ2txaGtpRzl3MEJBUXNGQUFPQwpBUUVBWjlEcytzTjNuajZHVU4yZTh2WU9wYjFBL0dJQlNQeEJJU3RJelNabWluMlBzZnpuZ1lPWXZtSTF3Z2kzCnJpbVpnTTBsazY4ZVhDZUxLR3E4dUpuUWsrZW0xcDhja0NzSFRJekdyMDBOeTRORVdiYnJzMFRyT2RlTDZQcUoKSld2bmdadGhaV1pOOHRUMmM1NTJKdGYvTThEblExVkg5dDhDWXI2WDJaYWw2MjhjUjFOeGFZWm5XcFJiM2UrMQpGQWI0b2pJVHNFZVRNVHdhVlZsLytFLzRhU1VNaGhPVWE5SFFmWHpXd2RxOHc2R2gwY3FpbHcxRFZsT3RPeUxXClpJQ3JEVGNEaUJ0MUVneGc2Yi9oZlVuZzlyOEpxRi9wQmdKY3g3NmJFMGpkbWN6T2FPV24xbnRIaGlaS3VaSjYKaktGZ2VSeHI3YXpkQXJUcEVnbXMvbU9QVUE9PQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==
    server: https://192.168.0.201:16443
  name: microk8s-cluster
contexts:
- context:
    cluster: microk8s-cluster
    user: admin
  name: microk8s
current-context: microk8s
kind: Config
preferences: {}
users:
- name: admin
  user:
    token: aGlIU3paZDZvM1VQMnpaOUZ2UndINjNLOXg0N3RjUy9UMGJxTmlLaVNuMD0K

```
Copy the parts __clusters:__, __contexts:__ and __users__ to the file `${HOME}/.kube/config`. If the
file does not exsist at all, copy the while output into a nde config-file.

On the development machine a command `kubectl get nodes` should now response with simething like:
```bash
❯ kubectl get nodes
NAME        STATUS   ROLES    AGE   VERSION
lanzarote   Ready    <none>   63m   v1.27.4
elhierro    Ready    <none>   56m   v1.27.4
lapalma     Ready    <none>   56m   v1.27.4
```
