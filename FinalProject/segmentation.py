import cv2
import numpy as np


def show_image(img):
    cv2.imshow("preview", img)
    cv2.waitKey(0)
    cv2.destroyAllWindows()

#Drawing contour boundaries
def show_contour_bb(contours, img):
    for contour in contours:
        x, y, w, h = cv2.boundingRect(contour) 
        cv2.rectangle(img, (x, y), (x + w, y + h), (0, 255, 0), 1)
    show_image(img)


#egments lines in an image by extracting contours and then cropping the image based on the bounding rectangles of these contours.
def segment_lines(img):
    height, width, _ = img.shape

    img_gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    img_binary = cv2.adaptiveThreshold(img_gray, 255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY_INV, 21, 20)

    kernel = np.ones(shape=(1, 1000), dtype=np.uint8)
    img_closed = cv2.morphologyEx(img_binary, cv2.MORPH_CLOSE, kernel)
    show_image(img_closed)

    contours, _ = cv2.findContours(img_closed, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    contours = sorted(contours, key=lambda cntr: cv2.boundingRect(cntr)[1])
    show_contour_bb(contours, img)

    segmented_lines = []
    for contour in contours:
        x, y, w, h = cv2.boundingRect(contour)

        img_crop = img[y-25:y + h + 25, x:x + w]
        show_image(img_crop)

        segmented_lines.append(img_crop)

    return segmented_lines


#This function segments characters in an image using a series of image processing steps, including adaptive thresholding, closing, contour extraction, and bounding box manipulation.
def segment_image(img):
    height, width, _ = img.shape
    print(f"Height: {height}\n Width: {width}")

    img_gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    show_image(img_gray)

    img_binary = cv2.adaptiveThreshold(img_gray, 255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY_INV, 21, 20)
    show_image(img_binary)

    kernel = np.ones(shape=(50, 1), dtype=np.uint8)
    img_closed = cv2.morphologyEx(img_binary, cv2.MORPH_CLOSE, kernel)
    kernel2 = np.ones(shape=(1, 10), dtype=np.uint8)
    img_closed = cv2.morphologyEx(img_closed, cv2.MORPH_CLOSE, kernel2)
    show_image(img_closed)

    contours, _ = cv2.findContours(img_closed, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    contours = sorted(contours, key=lambda cntr: cv2.boundingRect(cntr)[0])

    contours, _ = cv2.findContours(img_closed, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    # Define the padding value (adjust as needed)
    padding = 5
    # Expand each bounding rectangle by adding padding
    contours_with_padding = []
    for contour in contours:
        area = cv2.contourArea(contour)
        print("area")
        print(area)
        x, y, w, h = cv2.boundingRect(contour)
        print("height")
        print(h)
        print("width")
        print(w)
        if area>400 and w>10 and h>7:
            # Add padding to the bounding rectangle
            x -= padding
            y -= padding
            w += padding
            h += (padding)
            h+=10
            # Ensure that the adjusted rectangle is within the image boundaries
            x = max(x, 0)
            y = max(y, 0)
            w = min(w, img_closed.shape[1] - x)
            h = min(h, img_closed.shape[0] - y)

            # Create a new contour based on the adjusted rectangle
            contours_with_padding.append(np.array([[x, y], [x+w, y], [x+w, y+h], [x, y+h]]))

        # Optionally, sort the contours with padding based on x-coordinate
        contours = sorted(contours_with_padding, key=lambda cntr: cv2.boundingRect(cntr)[0])


    show_contour_bb(contours, img)
    print("after contoru__bb")
    segmented_images = []
    for contour in contours:
        x, y, w, h = cv2.boundingRect(contour)
        print("x "+str(x)+" y "+str(y)+" w "+str(w)+" h "+str(h) )
        # img_crop = img_binary[y-10:y + h + 10, x-10:x + w + 10]
        img_crop = img_binary[y-5:y + h + 5, x-5:x + w + 5]
        img_crop = img_binary[y:y + h, x:x + w]
        show_image(img_crop)
        # print("here")
        img_resize = cv2.resize(img_crop, (28, 28), interpolation=cv2.INTER_AREA)
        print(img_crop.shape)
        if img_crop is not None and not img_crop.size == 0:
            # Attempt to resiz e only if img_crop is not empty
            img_resize = cv2.resize(img_crop, (28, 28), interpolation=cv2.INTER_AREA)
            _, img_resize_thresh = cv2.threshold(img_resize, 15, 255, cv2.THRESH_BINARY)
        else:
            print("Error: Empty or invalid image in img_crop")
            # Handle the case where img_crop is empty, e.g., set img_resize to a default value
            img_resize = None
            img_resize_thresh = None

        show_image(img_resize)

        _, img_resize_thresh = cv2.threshold(img_resize, 15, 255, cv2.THRESH_BINARY)
        show_image(img_resize_thresh)

        # kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (2, 2))
        # img_dilated = cv2.dilate(img_resize_thresh, kernel, iterations=1)
        # show_image(img_dilated)
        segmented_images.append(img_resize_thresh)

    return segmented_images


if __name__ == '__main__':
    image = cv2.imread("")
    # show_image(image)
    # segment_image(image)
    segment_lines(image)
