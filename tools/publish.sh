!/bin/bash

if [ "$TRAVIS_REPO_SLUG" == "hwolf/oauth2" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

  echo -e "Publishing reports...\n"

  cp -R build/reports $HOME/reports-latest

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/ReadyTalk/swt-bling gh-pages > /dev/null

  cd gh-pages
  git rm -rf ./reports
  cp -Rf $HOME/reports-latest ./reports
  git add -f .
  git commit -m "Lastest reports on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo -e "Published reports to gh-pages.\n"
  
fi
