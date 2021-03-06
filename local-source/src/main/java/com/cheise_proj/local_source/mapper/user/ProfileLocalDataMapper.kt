package com.cheise_proj.local_source.mapper.user

import com.cheise_proj.data.model.user.ProfileData
import com.cheise_proj.local_source.mapper.base.LocalMapper
import com.cheise_proj.local_source.model.user.ProfileLocal
import javax.inject.Inject

class ProfileLocalDataMapper @Inject constructor() :
    LocalMapper<ProfileLocal, ProfileData> {
    override fun localToData(l: ProfileLocal): ProfileData {
        return ProfileData(
            username = l.username,
            semester = l.semester,
            section = l.section,
            refNo = l.refNo,
            guardian = l.guardian,
            gender = l.gender,
            faculty = l.faculty,
            contact = l.contact,
            adminDate = l.adminDate,
            dob = l.dob,
            imageUrl = l.imageUrl,
            level = l.level,
            name = l.name
        )
    }

    override fun dataToLocal(d: ProfileData): ProfileLocal {
        return ProfileLocal(
            username = d.username,
            semester = d.semester,
            section = d.section,
            refNo = d.refNo,
            guardian = d.guardian,
            gender = d.gender,
            faculty = d.faculty,
            contact = d.contact,
            adminDate = d.adminDate,
            dob = d.dob,
            imageUrl = d.imageUrl,
            level = d.level,
            name = d.name
        )
    }

    override fun localToDataList(lList: List<ProfileLocal>): List<ProfileData> {
        throw NotImplementedError("not implemented")
    }

    override fun dataToLocalList(dList: List<ProfileData>): List<ProfileLocal> {
        throw NotImplementedError("not implemented")
    }
}