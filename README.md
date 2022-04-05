# docker

## docker-compose with custom `custom.env` file

```shell
sudo docker-compose --env-file custom.env up -d
sudo docker-compose --env-file custom.env down
```

Environment variables are not automatically populated into the container.
You can populate all env variables from the file with:

```yml
env_file:
  - stack.env
```

More details here, scroll to the bottom.
[https://docs.portainer.io/v/ce-2.11/user/docker/stacks/add](https://docs.portainer.io/v/ce-2.11/user/docker/stacks/add)