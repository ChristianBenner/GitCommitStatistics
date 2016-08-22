#!/bin/bash
if [ -d "cloned" ]; then
	print "cloned EXISTS!"
	rm -rf "cloned"
fi
mkdir cloned
git clone $1 cloned
git -C cloned/.git log --name-only --pretty=format:'>>>%ce %cd' --date=short > gitlog.log
java -jar "gcs.jar" $2 $3