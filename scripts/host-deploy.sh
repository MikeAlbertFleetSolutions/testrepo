#!/usr/bin/env bash
set -ex

sudo systemctl stop accounting-processor@${DEPLOY_ENV}
tar -xvf /tmp/accounting-processor.tar -C /opt -m --no-overwrite-dir # Note that file access/modified times might be wrong
sudo systemctl start accounting-processor@${DEPLOY_ENV}
rm /tmp/accounting-processor.tar