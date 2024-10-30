from PIL import Image
import sys
import os
import shutil

def hex_to_rgb(hex_color):
    """Converts a hex color code to an RGB tuple."""
    hex_color = hex_color.lstrip('#')
    return tuple(int(hex_color[i:i+2], 16) for i in (0, 2, 4))

def apply_color_to_grayscale_image(image_path, hex_color, output_path):
    """Takes a grayscale image with transparency and applies color based on brightness."""
    # Open the image, including alpha channel (transparency)
    image = Image.open(image_path).convert('RGBA')

    # Get the RGB values from the hex color
    color_rgb = hex_to_rgb(hex_color)

    # Create a new image with 'RGBA' mode to preserve transparency
    colored_image = Image.new("RGBA", image.size)

    # Apply the color to the grayscale image based on the brightness, preserving transparency
    for x in range(image.width):
        for y in range(image.height):
            # Get the pixel's RGBA values
            r, g, b, alpha = image.getpixel((x, y))

            # Only apply color if the pixel is not fully transparent
            if alpha > 0:
                # Calculate the brightness based on the grayscale intensity of the RGB values
                brightness = r / 255.0  # Since it's a grayscale image, only 'r' is enough

                # Calculate the new RGB values based on brightness
                new_pixel = tuple(int(brightness * component) for component in color_rgb)

                # Set the new pixel in the colored image, keeping the original alpha (transparency)
                colored_image.putpixel((x, y), (*new_pixel, alpha))
            else:
                # If pixel is fully transparent, retain transparency
                colored_image.putpixel((x, y), (0, 0, 0, 0))

    # Save the resulting image
    colored_image.save(output_path)
    print(f"Image saved as {output_path}")

def clearOutputFolder():
    folder = './images/output'
    for filename in os.listdir(folder):
        file_path = os.path.join(folder, filename)
        try:
            if os.path.isfile(file_path) or os.path.islink(file_path):
                os.unlink(file_path)
            elif os.path.isdir(file_path):
                shutil.rmtree(file_path)
        except Exception as e:
            print('Failed to delete %s. Reason: %s' % (file_path, e))

if __name__ == "__main__":
    clearOutputFolder()

    hex_color = '#706464'
    name = "hafnium"

    input = './images/input/ingot.png'
    output = './images/output/' + name + '_ingot.png'
    apply_color_to_grayscale_image(input, hex_color, output)

    input = './images/input/block.png'
    output = './images/output/' + name + '_block.png'
    apply_color_to_grayscale_image(input, hex_color, output)

    input = './images/input/dust.png'
    output = './images/output/' + name + '_dust.png'
    apply_color_to_grayscale_image(input, hex_color, output)

    input = './images/input/gear.png'
    output = './images/output/' + name + '_gear.png'
    apply_color_to_grayscale_image(input, hex_color, output)

    input = './images/input/nugget.png'
    output = './images/output/' + name + '_nugget.png'
    apply_color_to_grayscale_image(input, hex_color, output)

    input = './images/input/plate.png'
    output = './images/output/' + name + '_plate.png'
    apply_color_to_grayscale_image(input, hex_color, output)

    input = './images/input/rod.png'
    output = './images/output/' + name + '_rod.png'
    apply_color_to_grayscale_image(input, hex_color, output)

    input = './images/input/raw.png'
    output = './images/output/' + name + '_raw.png'
    apply_color_to_grayscale_image(input, hex_color, output)