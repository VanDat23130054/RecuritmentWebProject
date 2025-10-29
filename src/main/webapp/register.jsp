<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BaiTapTuan4 - Register Form</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<h2>User Registration Form</h2>

<form action="register" method="post">
    <!-- Name -->
    <label>First Name:</label>
    <label>
        <input type="text" name="firstName" required>
    </label>

    <label>Last Name:</label>
    <label>
        <input type="text" name="lastName" required>
    </label>

    <!-- Email & Password -->
    <label>Email:</label>
    <label>
        <input type="email" name="email" required>
    </label>

    <label>Password:</label>
    <label>
        <input type="password" name="password" required>
    </label>

    <!-- Gender -->
    <label>Gender:</label>
    <div class="radio-group">
        <label><input type="radio" name="female" value="true"> Female</label>
        <label><input type="radio" name="female" value="false"> Male</label>
    </div>

    <!-- Year of Birth -->
    <label>Year of Birth:</label>
    <label>
        <input type="number" name="yearOfBirth" min="1900" max="2025" required>
    </label>

    <!-- Industry -->
    <label>Industry:</label>
    <label>
        <select name="industry">
            <option value="" disabled selected hidden>[Select industry]</option>
            <option value="Computer Software">Computer Software</option>
            <option value="Consulting">Consulting</option>
            <option value="Distribution">Distribution</option>
            <option value="Education">Education</option>
        </select>
    </label>

    <!-- Job & Company -->
    <label>Job Title:</label>
    <label>
        <input type="text" name="jobTitle">
    </label>

    <label>Company:</label>
    <label>
        <input type="text" name="company">
    </label>

    <!-- City -->
    <label>City:</label>
    <label>
        <input type="text" name="city">
    </label>

    <!-- Telephone -->
    <label>Telephone:</label>
    <label>
        <input type="tel" name="telephone" pattern="[0-9]{10}" placeholder="e.g. 0912345678">
    </label>

    <!-- Favorites -->
    <label>Favorites:</label>
    <div class="checkbox-group">
        <label><input type="checkbox" name="favorites" value="music"> Music</label>
        <label><input type="checkbox" name="favorites" value="art"> Art</label>
        <label><input type="checkbox" name="favorites" value="sports"> Sports</label>
        <label><input type="checkbox" name="favorites" value="reading"> Reading</label>
    </div>

    <!-- Desired Platform -->
    <label>Desired Platform:</label>
    <label>
        <select name="desiredPlatform">
            <option value="" disabled selected hidden>[Select desired platform] </option>
            <option value="Web">Web</option>
            <option value="Mobile">Mobile</option>
            <option value="Desktop">Desktop</option>
        </select>
    </label>

    <div class="button-group">
        <input type="submit" value="Register">
        <input type="reset" value="Reset">
    </div>

</form>
</body>
</html>