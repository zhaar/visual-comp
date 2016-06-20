
# Problems encountered

The whole Image processing is a java project and is compiled to a .Jar file that you can find in the `libraries` fodler.

Running the applet involves placing the jar in processing's library folder and running gamePDE.pde.

One issue that can happen if both the library for video and the jar are in libraries is that they have conflicting imports and processing can't figure out which one to pick. In that case simply delete the video library.

Another issue is that depending on your install, you might get something like 

    java.lang.UnsatisfiedLinkError: Could not load library: gstreamer
    A library relies on native code that's not available.
    Or only works properly when the sketch is run as a 32-bit application.

For which I haven't found a solution. 
