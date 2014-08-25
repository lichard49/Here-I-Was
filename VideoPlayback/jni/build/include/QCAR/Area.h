/*==============================================================================
Copyright (c) 2010-2013 QUALCOMM Austria Research Center GmbH.
All Rights Reserved.
Proprietary - QUALCOMM Austria Research Center GmbH.

@file 
    Area.h

@brief
    Header file for Area class.
==============================================================================*/


#ifndef _QCAR_AREA_H_
#define _QCAR_AREA_H_


#include <QCAR/QCAR.h>


namespace QCAR
{


/// Area is the base class for 2D shapes used in QCAR
class QCAR_API Area
{
public:
    enum TYPE {
        RECTANGLE,
        RECTANGLE_INT,
        INVALID
    };

    virtual TYPE getType() const = 0;

    virtual ~Area();

private:
    Area& operator=(const Area& other);
};


} // namespace QCAR


#endif // _QCAR_AREA_H_
