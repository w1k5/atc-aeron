#!/bin/zsh

# Ensure globstar is enabled for recursive globbing (useful for zsh)
setopt globstar

# Install PlantUML if not installed
if ! command -v plantuml &> /dev/null
then
    echo "PlantUML could not be found. Installing..."
    sudo apt-get update
    sudo apt-get install plantuml
fi

# Generate diagrams from .puml files
echo "Generating diagrams..."
plantuml -v -tsvg **/*.puml

# Commit and push the generated diagrams
echo "Committing and pushing diagrams..."
git add **/*.svg
git commit -m "Update generated diagrams" || exit 0
git push origin main

echo "Done!"
9