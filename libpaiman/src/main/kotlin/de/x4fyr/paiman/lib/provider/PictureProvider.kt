package de.x4fyr.paiman.lib.provider


/**
 * Interface for picture providing actions
 */
interface PictureProvider {
    /**
     * Actions that picks a picture
     *
     * @param onReturn action to run, when a picture is picked. Accept the url, that might be null on error/cancel
     */
    fun pickPicture(onReturn: (url: String?) -> Unit)
}