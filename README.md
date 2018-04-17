# Capstone-Project

<h3>Description</h3>  
 
This app is going to be a small company app and useful for me and my colleagues. At present we use any kind of paper (which is often lost) to record the location and dimensions of the holes that we isolate with fire protection materials. After that the team leader take the notes of each workers and walk around holes taking pictures and putting check label on them. Separately he must calculate the price of each hole based on a price list depends on its size. All this happens slowly and takes a long time. I think this app help us to reduce time and accuracy for processing the information and finally the information will be permanently stored accessible for any statistics. 
<h3>Udacity Capstone Project</h3>

For very first time when application starts, worker must authenticate yourself by username and password given from team manager. The successful authentication will load Price and Place no editable lists which are needed to add notes. They can be viewed from main menu, clicking on <i>Price list</i> and <i>Place list</i> commands.

The main activity contains a list of all notes created by current worker sorted by date and float action bar button that allows adding new note.

<h4>Note card contains:</h4>
    
    - information for dimension, location and price;
    - action buttons for viewing and editing;
    - all above information on the top of image background (if such exists)

Each note could be viewed and edited. The last action is available before upload command happened. Once it happened note could not be changed by app requirements.
<h4>Add new note</h4>

To add new note at least place and dimension must be chosen and entered. Otherwise add action at the action bar is disabled. Dimension must be in format:

    width x height [ x layer_numbers [ x copies]]
    
Image(s) could be added simultaneously or later by switching to note edit mode.
<h4>Edit note</h4>
Note could be edited and functionality is similar to <i>add</i> action. But once <i>Upload</i> action done note become no editable (by design).
<h4>Detail view</h4>
Detail view show additional information for selected note in <i>ViewPager</i> and user can
slide to previous and next note detail information.
<h4>Upload notes</h4>
User have to upload all generate notes by 
<i>Upload</i> action from main menu 
(to Firebase database and storage). Only notes that contain image(s) could be uploaded (according by application requirements).