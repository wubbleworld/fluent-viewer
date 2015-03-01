# fluent-viewer

Visualization tool for investigating Wubble World output runs.  There are two parts to the output of a Wubble World run.  The first is the raw locations, speeds and object details represented as floats and the second are sensors that detect decreasing, increasing, stable across the different combinations of objects.  So two objects getting closer will have a single float representing the current distance and a boolean value containing true if the distance is increasing and false otherwise.  This is a slight simplification, but it gives you an idea for the data being stored in the state.db database found in the data/ directory.

This tool allowed us to plot and visualize the details of a single run through wubble world.

