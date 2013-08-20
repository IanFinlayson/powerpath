#ifndef Vector2D_H
#define Vector2D_H

class Vector2d
{
public:
	float x;
	float y;
	
	//constructor
	Vector2d();
	Vector2d(float xCoord, float yCoord)
	{
		x = xCoord;
		y = yCoord;
		
	}
};

#endif