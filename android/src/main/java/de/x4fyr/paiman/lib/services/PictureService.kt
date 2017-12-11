package de.x4fyr.paiman.lib.services


/**
 * Interface for picture providing actions
 */
interface PictureService {
    /**
     * Actions that picks a picture
     *
     * @param onReturn action to run, when a picture is picked. Accept the url, that might be null on error/cancel
     */
    fun pickPicture(onReturn: (url: String?) -> Unit)
}