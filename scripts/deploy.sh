#!/usr/bin/env bash
set -x

GIT_SHA1=$1
BUILD_NUM=$2
DEPLOY_ENV=$3
SYNC_SOURCE=$4
GIT_PREV_SHA1=$5


ARTIFACT_TAR="/tmp/accounting-processor-$GIT_SHA1.tar"
ARTIFACT_TMP="/tmp/accounting-processor/$GIT_SHA1"
APP_PACK_DIR="/tmp/accounting-processor/pack-$GIT_SHA1/accounting-processor-${DEPLOY_ENV}"
APP_TAR="/tmp/accounting-processor/pack-$GIT_SHA1.tar"

GIT_SOURCE_REPO_URL="git@github.com:MikeAlbertFleetSolutions/accounting-processor.git"
GIT_CONFIG_REPO_URL="git@git.mikealbert.corp:accounting-processor/${DEPLOY_ENV}.git"

SOURCE_DIR=/home/netadmin/accounting-processor/src

# where we and our source are
HERE_DIR=$(readlink -f "$(dirname "$0")")

source /home/netadmin/jfrog-creds.sh

#########################
# Getting the changelog #
#########################

# get build details for notifications
cd "${SOURCE_DIR}"
if [[ "${GIT_PREV_SHA1}" == "" ]]; then
  GIT_PREV_SHA1="$(git log -1 --format='%H')"
else
  # get the commit details, make json safe string
  DISCORD_DETAILS="$(git log --pretty=format:'%cn: %s' "${GIT_PREV_SHA1}..${GIT_SHA1}" | jq --slurp --raw-input '.')"

  # jq creates a string with quotes as first and last character, remove those
  DISCORD_DETAILS="${DISCORD_DETAILS:1:${#DISCORD_DETAILS}-2}"
fi
cd "${HERE_DIR}"

# git source files
if [ -e "${SOURCE_DIR}/.git" ]
then
  cd "${SOURCE_DIR}"
else
  mkdir -p "${SOURCE_DIR}"
  cd "${SOURCE_DIR}"
  git clone -o source "$GIT_SOURCE_REPO_URL" .
fi

git remote set-url source "${GIT_SOURCE_REPO_URL}" || true
git fetch --force source "master:remotes/origin/master"
git reset --hard "${GIT_SHA1}"

# just getting new version of this file
if [[ "${SYNC_SOURCE}" != "" ]];
then
  # execute self, don't stop here next time
  bash "${HERE_DIR}/deploy.sh" "${GIT_SHA1}" "${BUILD_NUM}" "${DEPLOY_ENV}" "" "${GIT_PREV_SHA1}"

  exit 0
fi

###################################
# Building the deployment package #
###################################

# Get the build artifacts from jfrog
curl -X POST \
  https://mafs.jfrog.io/mafs/api/archive/buildArtifacts \
  -u "$JFROG_USER:$JFROG_PASS" \
  -H 'content-type: application/json' \
  -d "{\"buildName\":\"accounting-processor\",\"buildNumber\":\"$GIT_SHA1\",\"archiveType\":\"tar\"}" \
  --output "$ARTIFACT_TAR"

mkdir -p "$ARTIFACT_TMP"
tar -xf "$ARTIFACT_TAR" -C "$ARTIFACT_TMP"
rm "$ARTIFACT_TAR"
# Get the jar
TMP_JAR_FILE=`find "$ARTIFACT_TMP" -name '*.jar' | head -1`
# Prepare the app package with the configuration
git clone "$GIT_CONFIG_REPO_URL" "$APP_PACK_DIR"
# Copy the appliaction jar to the package
cp "$TMP_JAR_FILE" "$APP_PACK_DIR/accounting-processor.jar"
# Generate the appliaction package tar
tar -cf "$APP_TAR" -C "$APP_PACK_DIR/.." --exclude-vcs "accounting-processor-${DEPLOY_ENV}"
# Clean up after ourselfes
rm -rf "$ARTIFACT_TMP" "/tmp/accounting-processor/pack-$GIT_SHA1"

######################
# DEPLOY STARTS HERE #
######################

if [[ "$DEPLOY_ENV" == "prod" ]]; then
  # hosts to deploy to
  REMOTE_HOSTS=(
    "pdoc1.mikealbert.corp"
    "pdoc2.mikealbert.corp"
    "pdoc3.mikealbert.corp"
  )
else
  # hosts to deploy to
  REMOTE_HOSTS=(
    "sdoc1.mikealbert.corp"
    "sdoc2.mikealbert.corp"
    "sdoc3.mikealbert.corp"
  )
fi

# how many hosts the app has been deployed to
DEPLOYED_HOSTS=0

COLOR=1

for REMOTE_HOST in ${REMOTE_HOSTS[*]}; do
    echo -e "Deploying to \"\e[3${COLOR}m${REMOTE_HOST}\e[39m\"\e[3${COLOR}m"
    scp $APP_TAR netadmin@$REMOTE_HOST:/tmp/accounting-processor.tar && \
    cat ${HERE_DIR}/host-deploy.sh | ssh netadmin@$REMOTE_HOST DEPLOY_ENV=${DEPLOY_ENV} bash
    if [[ $? == 0 ]]; then
      DEPLOYED_HOSTS=$((DEPLOYED_HOSTS+1))
    else
      break
    fi
    echo -en "\e[39m"
    COLOR=$((COLOR+1))
done
echo -en "\e[39m"
if [[ "${DEPLOYED_HOSTS}" == "${#REMOTE_HOSTS[@]}" ]]; then
    if [[ "$DEPLOY_ENV" == "prod" ]]; then
     # create change management ticket with details
      echo -e "${DISCORD_DETAILS}" | mailx -s "accounting-processor build ${BUILD_NUM} deployed to ${DEPLOY_ENV}" -r "netadmin@mikealbert.com" "helpdesk@mikealbert.com"
    fi

    rm $APP_TAR
    /usr/local/bin/taz -channel accounting-and-finance -embed "{\"title\":\"accounting-processor build ${BUILD_NUM} deployed to ${DEPLOY_ENV}\",\"description\":\"${DISCORD_DETAILS}\",\"color\":8436545}"
else
    /usr/local/bin/taz -channel accounting-and-finance -embed "{\"title\":\"accounting-processor build ${BUILD_NUM} DEPLOY FAILED to ${DEPLOY_ENV}\",\"description\":\"${DISCORD_DETAILS}\",\"color\":15612208}"
    rm $APP_TAR
    exit 22
fi

