from PIL import Image
import sys

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

if __name__ == "__main__":
    # Example usage:
    # python script.py input_image.png #ff5733 output_image.png

    # Set the path to the input and output image and hex color code
    input_image_path = 'input_image.png'  # Replace with your grayscale image
    hex_color = '#b9f0f0'  # Replace with your desired hex color
    output_image_path = 'output_image.png'  # The colored output image

    apply_color_to_grayscale_image(input_image_path, hex_color, output_image_path)