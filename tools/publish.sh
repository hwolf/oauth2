!/bin/bash

if [ "$TRAVIS_REPO_SLUG" == "hwolf/oauth2" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

  echo -e "Publishing artifacts and reports...\n"

  cp -R build/reports $HOME/reports-latest
  mkdir -p $HOME/artifacts-latest
  cp -R server-*/build/libs/*.jar $HOME/artifacts-latest

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/hwolf/oauth2 gh-pages > /dev/null

  cd gh-pages
  git rm -rf ./reports
  cp -Rf $HOME/reports-latest ./reports
  git add -f .

  git rm -rf ./artifacts
  cp -Rf $HOME/artifacts-latest ./artifacts
  git add -f .

  git commit -m "Lastest artifacts and reports on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo -e "Published artifacts and reports to gh-pages.\n"
  
fi

