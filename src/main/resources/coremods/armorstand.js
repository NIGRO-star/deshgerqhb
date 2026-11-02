// Patches ArmorStandEntity.hurt (SRG: func_70097_a, 1.16.5) so that, at the very start,
// it asks ArmorStandDamage.handleFire(this, source, amount) and returns false (damage handled)
// when the mod took care of a fire-type damage source.
//
// Equivalent Java inserted at the top of the method:
//     if (com.infestationpotion.ArmorStandDamage.handleFire(this, source, amount)) return false;
//
// Local variables of the instance method: 0 = this, 1 = source, 2 = amount (float).

var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');

function initializeCoreMod() {
    return {
        'infestationpotion_armor_stand_hurt': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.entity.item.ArmorStandEntity',
                'methodName': 'func_70097_a',
                'methodDesc': '(Lnet/minecraft/util/DamageSource;F)Z'
            },
            'transformer': function (method) {
                var skip = new LabelNode();
                var list = new InsnList();
                list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                list.add(new VarInsnNode(Opcodes.ALOAD, 1));
                list.add(new VarInsnNode(Opcodes.FLOAD, 2));
                list.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    'com/infestationpotion/ArmorStandDamage',
                    'handleFire',
                    '(Lnet/minecraft/entity/item/ArmorStandEntity;Lnet/minecraft/util/DamageSource;F)Z',
                    false));
                list.add(new JumpInsnNode(Opcodes.IFEQ, skip));
                list.add(new InsnNode(Opcodes.ICONST_0));
                list.add(new InsnNode(Opcodes.IRETURN));
                list.add(skip);
                method.instructions.insert(list);
                return method;
            }
        }
    };
}
