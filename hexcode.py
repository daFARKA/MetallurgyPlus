from PIL import Image
import numpy as np

# Load the images
grayscale_image = Image.open("input_image.png").convert("L")  # Convert to grayscale if needed
color_image = Image.open("output_image.png").convert("RGB")  # Ensure the color image is in RGB

# Ensure images are the same size
if grayscale_image.size != color_image.size:
    raise ValueError("Images must be the same size for comparison")

# Convert images to numpy arrays
grayscale_array = np.array(grayscale_image)
color_array = np.array(color_image)

# Dictionary to store grayscale-to-color mappings
grayscale_to_color = {}

# Loop through all pixels and map grayscale values to their color counterparts
for gray_value in np.unique(grayscale_array):
    # Find the corresponding color values for this grayscale value
    mask = grayscale_array == gray_value
    corresponding_colors = color_array[mask]

    # Average color (if there are multiple colors for a grayscale value)
    avg_color = np.mean(corresponding_colors, axis=0).astype(int)
    
    # Convert RGB to hex
    hex_color = "#{:02x}{:02x}{:02x}".format(avg_color[0], avg_color[1], avg_color[2])

    # Map the grayscale value to the hex color
    grayscale_to_color[gray_value] = hex_color

print(grayscale_to_color[255])